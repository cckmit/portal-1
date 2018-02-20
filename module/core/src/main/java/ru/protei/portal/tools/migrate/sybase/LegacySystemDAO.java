package ru.protei.portal.tools.migrate.sybase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import protei.sql.Tm_SqlHelper;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.tools.migrate.Const;
import ru.protei.portal.tools.migrate.HelperService;
import ru.protei.portal.tools.migrate.struct.*;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by turik on 08.09.16.
 */
public class LegacySystemDAO {

//    public static final String PORTAL_SYBASE_JDBC_URL = "jdbc:sybase:Tds:192.168.1.55:2638/PORTAL2017";

    private static Logger logger = LoggerFactory.getLogger(LegacySystemDAO.class);

    @Autowired
    private SybConnProvider connProvider;

    @Autowired
    PersonDAO personDAO;

    private String ourHost;

    @PostConstruct
    private void __init () {
        try {
            ourHost = Inet4Address.getLocalHost ().getHostAddress();
        }
        catch (Throwable e) {
            ourHost = Const.CREATOR_HOST_VALUE;
        }
    }

    public String getOurHost() {
        return ourHost;
    }

    public void saveExternalEmployee(Person person, String departmentName, String positionName) throws SQLException, UnknownHostException {
        if (person == null || person.getId () == null) return;
        if (isExistPerson(person)) {
            mergeEmployee(person, departmentName, positionName);
        } else {
            persistEmployee(person, departmentName, positionName);
        }
    }

    public void deleteExternalEmployee(Person person) throws SQLException, UnknownHostException {

        logger.debug ("deleteExternalEmployee(): person={}", person);

        try (Connection connection = connProvider.getConnection()) {
            ExternalPerson externalPerson = new ExternalPerson (person, "", "", ourHost);
            Tm_SqlHelper.updateObjectEx(connection, externalPerson);

            connection.commit ();
        }
    }

    private void persistEmployee(Person person, String departmentName, String positionName) throws SQLException, UnknownHostException {

        logger.debug ("persistEmployee(): person={}", person);

        try (Connection connection = connProvider.getConnection()) {
            ExternalPerson externalPerson = new ExternalPerson(person, departmentName, positionName, ourHost);
            Tm_SqlHelper.saveObjectEx(connection, externalPerson);

            ExternalPersonExtension externalPersonExtension = new ExternalPersonExtension(person);
            Tm_SqlHelper.saveObjectEx(connection, externalPersonExtension);

            connection.commit();

            person.setOldId(externalPerson.getId());
            personDAO.partialMerge(person, "old_id");
        }
    }

    private void mergeEmployee(Person person, String departmentName, String positionName) throws SQLException, UnknownHostException {

        logger.debug ("mergeEmployee(): person={}", person);

        try (Connection connection = connProvider.getConnection()) {

            ExternalPerson externalPerson = new ExternalPerson (person, departmentName, positionName, ourHost);
            Tm_SqlHelper.updateObjectEx(connection, externalPerson);

            ExternalPersonExtension externalPersonExtension = new ExternalPersonExtension (person);
            Tm_SqlHelper.updateObjectEx(connection, externalPersonExtension);

            connection.commit ();
        }
    }

    public boolean isExistsPerson (long id) throws SQLException {
        try (Connection connection = connProvider.getConnection()){
            return Tm_SqlHelper.exists(connection, ExternalPerson.class, id);
        }
    }

    public boolean isExistPerson(Person person) throws SQLException {
        return isExistsPerson(person.getOldId());
    }



    public ExternalProduct getExternalProduct (long id) throws SQLException {
        return runAction(transaction -> transaction.dao(ExternalProduct.class).get(id));
    }

    public ExternalCompany getExternalCompany (long id) throws SQLException {
        return runAction(transaction -> transaction.dao(ExternalCompany.class).get(id));
    }

    public ExternalPerson getExternalPerson (long id) throws SQLException {
        return runAction(transaction -> transaction.dao(ExternalPerson.class).get(id));
    }

    public ExternalPersonInfoCollector personCollector (List<ExternalPerson> personList) {
        return new ExternalPersonInfoCollector().fromPersonSet(personList);
    }

    public ExternalPersonInfoCollector personCollectorExt (List<ExternalPersonExtension> extensionList) {
        return new ExternalPersonInfoCollector().fromExtSet(extensionList);
    }

    public class ExternalPersonInfoCollector {

        private List<Long> keys;
        private Map<Long, ExternalPerson> personMap;
        private Map<Long, ExternalPersonExtension> extensionMap;

        public ExternalPersonInfoCollector() {
        }

        ExternalPersonInfoCollector fromKeys (List<Long> keys) {
            this.keys = keys;
            return this;
        }

        ExternalPersonInfoCollector fromPersonSet (List<ExternalPerson> personList) {
            this.personMap = HelperService.map(personList, person -> person.getId());
            this.keys = new ArrayList<>(personMap.keySet());
            return this;
        }

        ExternalPersonInfoCollector fromExtSet (List<ExternalPersonExtension> extensionList) {
            this.extensionMap = HelperService.map(extensionList, ext -> ext.getPersonId());
            this.keys = new ArrayList<>(extensionMap.keySet());
            return this;
        }

        public List<ExternalPersonInfo> asList (LegacyDAO_Transaction transaction) throws SQLException {
            List<ExternalPersonInfo> result = new ArrayList<>(asMap(transaction).values());
            Collections.sort(result, Comparator.comparing(ExternalPersonInfo::getPersonId));
            return result;
        }

        public Map<Long, ExternalPersonInfo> asMap (LegacyDAO_Transaction transaction) throws SQLException {
            if (personMap == null) {
                personMap = HelperService.map(transaction.dao(ExternalPerson.class).list(keys), person -> person.getId());
            }

            if (extensionMap == null) {
                extensionMap = HelperService.map(transaction.dao(ExternalPersonExtension.class).list(keys), ext -> ext.getPersonId());
            }

            final Map<Long, ExternalPersonInfo> tmp = new HashMap<>();
            keys.forEach(id -> {
                tmp.put(id, new ExternalPersonInfo(personMap.get(id), extensionMap.get(id)));
            });

            transaction.dao(ExtContactProperty.class)
                    .list("nPersonID", keys)
                    .forEach(cont -> tmp.get(cont.personId).addContactData(cont));

            return tmp;
        }
    }



    public <R> R runAction (LegacyDAO_Action<R> action) throws SQLException {
        try (LegacyDAO_Transaction daoFactory = new LegacyDAO_Transaction_impl(connProvider)){
            return action.doAction(daoFactory);
        }
    }

    public <R> R runActionRTE (LegacyDAO_Action<R> action) {
        try {
            return runAction (action);
        }
        catch (SQLException e) {
            logger.error("sql-error (action)", e);
            throw new RuntimeException(e);
        }
    }



    public interface LegacyEntityDAO<T extends LegacyEntity> {
        boolean exists (Long id) throws SQLException;
        boolean exists (String cond, Object...args) throws SQLException;

        T get (Long id) throws SQLException;
        T get (String cond, Object...args) throws SQLException;

        <K> List<T> list (Collection<K> keys) throws SQLException;
        <K> List<T> list (String column, Collection<K> values) throws SQLException;
        List<T> list (String cond, Object...args) throws SQLException;
        List<T> list () throws SQLException;

        T insert (T entity) throws SQLException;
        T update (T entity) throws SQLException;

        default T store (T entity) throws SQLException {
            return (exists(entity.getId())) ? update(entity) : insert(entity);
        }

        T delete (T entity) throws SQLException;
        void delete (Long id) throws SQLException;
    }

    public interface LegacyDAO_Transaction extends AutoCloseable {
        <T extends LegacyEntity> LegacyEntityDAO<T> dao(Class<T> type) throws SQLException;
        void commit () throws SQLException;
        void close();
    }

    public interface LegacyDAO_Action<R> {
        R doAction (LegacyDAO_Transaction transaction) throws SQLException;
    }

    static class LegacyDAO_Transaction_impl implements LegacyDAO_Transaction {
        private Connection connection;
        private Map<Class<?>, LegacyEntityDAO<?>> daoMap;

        public LegacyDAO_Transaction_impl(SybConnProvider provider) throws SQLException {
            this.connection = provider.getConnection();
            this.connection.setAutoCommit(false);
            this.daoMap = new HashMap<>();
        }

        @Override
        public void commit() throws SQLException {
            this.connection.commit();
        }

        @Override
        public <T extends LegacyEntity> LegacyEntityDAO<T> dao(Class<T> type) throws SQLException {
            return (LegacyEntityDAO<T>)daoMap.computeIfAbsent(type, aClass -> new LegacyEntityDAO_Impl<>(type, connection));
        }

        @Override
        public void close() {
            Tm_SqlHelper.safeCloseConnection(connection, true);
            daoMap.clear();
            daoMap = null;
            connection = null;
        }
    }


    static class LegacyEntityDAO_Impl<T extends LegacyEntity> implements LegacyEntityDAO<T> {

        Class<T> entityType;
        Connection connection;

        public LegacyEntityDAO_Impl(Class<T> entityType, Connection connection) {
            this.entityType = entityType;
            this.connection = connection;
        }

        @Override
        public boolean exists(Long id) throws SQLException {
            return id != null && Tm_SqlHelper.exists(connection, entityType, id);
        }

        @Override
        public boolean exists(String cond, Object... args) throws SQLException {
            return Tm_SqlHelper.exists(connection, entityType, cond, args);
        }

        @Override
        public <K> List<T> list(Collection<K> keys) throws SQLException {
            return Tm_SqlHelper.getObjectListEx(connection, entityType, keys);
        }

        @Override
        public <K> List<T> list(String column, Collection<K> values) throws SQLException {
            return Tm_SqlHelper.getObjectListExSafe(connection, entityType, column, values);
        }

        @Override
        public T get(Long id) throws SQLException {
            return id == null ? null : Tm_SqlHelper.getObjectEx(connection, entityType, id);
        }

        @Override
        public T get(String cond, Object... args) throws SQLException {
            return Tm_SqlHelper.getObjectEx(connection, entityType, cond, args);
        }

        @Override
        public List<T> list(String cond, Object... args) throws SQLException {
            return Tm_SqlHelper.getObjectsListEx(connection, entityType, cond, args);
        }

        @Override
        public List<T> list() throws SQLException {
            return Tm_SqlHelper.getObjectsListEx(connection, entityType);
        }

        @Override
        public T insert(T entity) throws SQLException {
            Tm_SqlHelper.saveObjectEx(connection, entity);
            return entity;
        }

        @Override
        public T update(T entity) throws SQLException {
            Tm_SqlHelper.updateObjectEx(connection, entity);
            return entity;
        }

        @Override
        public T delete(T entity) throws SQLException {
            Tm_SqlHelper.deleteObjectEx(connection, entity);
            return entity;
        }

        @Override
        public void delete(Long id) throws SQLException {
            if (id != null)
                Tm_SqlHelper.deleteObject(connection, entityType, id);
        }
    }
}
