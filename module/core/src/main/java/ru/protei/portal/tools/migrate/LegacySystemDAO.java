package ru.protei.portal.tools.migrate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import protei.sql.Tm_SqlHelper;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.tools.migrate.struct.*;
import ru.protei.portal.tools.migrate.sybase.SybConnProvider;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by turik on 08.09.16.
 */
public class LegacySystemDAO {

//    public static final String PORTAL_SYBASE_JDBC_URL = "jdbc:sybase:Tds:192.168.1.55:2638/PORTAL2017";

    private static Logger logger = LoggerFactory.getLogger(LegacySystemDAO.class);

    @Autowired
    private SybConnProvider connProvider;

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
//            String tableName = Tm_SqlHelper.getTableName (ExternalPerson.class);
//            String sql = Tm_SqlHelper.getUpdateString (externalPerson.getClass ());
//            logger.debug ("prepare statement = update {} set {} where nID = {}", tableName, sql, person.getId ());
//            PreparedStatement st = connection.prepareStatement("update " + tableName + " set " + sql + " where nID = " + person.getId ());
//            Tm_SqlHelper.setParams(st, externalPerson, true);
//            st.executeUpdate ();
//            st.close();
            connection.commit ();
        }
    }

    private void persistEmployee(Person person, String departmentName, String positionName) throws SQLException, UnknownHostException {

        logger.debug ("persistEmployee(): person={}", person);

        try (Connection connection = connProvider.getConnection()) {
            ExternalPerson externalPerson = new ExternalPerson(person, departmentName, positionName, ourHost);
            Tm_SqlHelper.saveObjectEx(connection, externalPerson);
//            String tableName = Tm_SqlHelper.getTableName(externalPerson.getClass());
//            String sql = Tm_SqlHelper.getInsertString(externalPerson.getClass(), false);
//            logger.debug("prepare statement = insert into {} {}", tableName, sql);
//            PreparedStatement st = connection.prepareStatement("insert into " + tableName + sql);
//            Tm_SqlHelper.setParams(st, externalPerson, false);
//            st.execute();
//            st.close();

            ExternalPersonExtension externalPersonExtension = new ExternalPersonExtension(person);
            Tm_SqlHelper.saveObjectEx(connection, externalPersonExtension);
//            tableName = Tm_SqlHelper.getTableName(externalPersonExtension.getClass());
//            sql = Tm_SqlHelper.getInsertString(externalPersonExtension.getClass(), false);
//            logger.debug("prepare statement = insert into {} {}", tableName, sql);
//            st = connection.prepareStatement("insert into " + tableName + sql);
//            Tm_SqlHelper.setParams(st, externalPersonExtension, false);
//            st.execute();
//            st.close();

            connection.commit();
        }
    }

    private void mergeEmployee(Person person, String departmentName, String positionName) throws SQLException, UnknownHostException {

        logger.debug ("mergeEmployee(): person={}", person);

        try (Connection connection = connProvider.getConnection()) {

            ExternalPerson externalPerson = new ExternalPerson (person, departmentName, positionName, ourHost);
            Tm_SqlHelper.updateObjectEx(connection, externalPerson);
//            String tableName = Tm_SqlHelper.getTableName (ExternalPerson.class);
//            String sql = Tm_SqlHelper.getUpdateString (externalPerson.getClass ());
//            logger.debug ("prepare statement = update {} set {} where nID = {}", tableName, sql, person.getId ());
//            PreparedStatement st = connection.prepareStatement("update " + tableName + " set " + sql + " where nID = " + person.getId ());
//            Tm_SqlHelper.setParams(st, externalPerson, true);
//            st.executeUpdate ();
//            st.close();

            ExternalPersonExtension externalPersonExtension = new ExternalPersonExtension (person);
            Tm_SqlHelper.updateObjectEx(connection, externalPersonExtension);
//            tableName = Tm_SqlHelper.getTableName (ExternalPersonExtension.class);
//            sql = Tm_SqlHelper.getUpdateString (externalPersonExtension.getClass ());
//            logger.debug ("prepare statement = update {} set {} where nID = {}", tableName, sql, person.getId ());
//            st = connection.prepareStatement("update " + tableName + " set " + sql + " where nID = " + person.getId ());
//            Tm_SqlHelper.setParams(st, externalPersonExtension, true);
//            st.executeUpdate ();
//            st.close();

            connection.commit ();
        }
    }

    public boolean isExistsPerson (long id) throws SQLException {
        try (Connection connection = connProvider.getConnection()){
            return Tm_SqlHelper.exists(connection, ExternalPerson.class, id);
        }
    }

    public boolean isExistPerson(Person person) throws SQLException {
        return isExistsPerson(person.getId());
    }

    public ExternalProduct getExternalProduct (long id) throws SQLException {
        return runAction(transaction -> transaction.dao(ExternalProduct.class).get(id));
    }

    public ExternalCompany getExternalCompany (long id) throws SQLException {
        return runAction(transaction -> transaction.dao(ExternalCompany.class).get(id));
    }

//    public ExternalCompany findExportCompany (long ourId) throws SQLException {
//        return runAction(transaction -> {
//            LegacyEntityDAO<ExternalCompany> dao = transaction.dao(ExternalCompany.class);
//            return findExportCompany(ourId, dao);
//        });
//    }
//
//    public ExternalCompany findExportCompany(long ourId, LegacyEntityDAO<ExternalCompany> dao) throws SQLException {
//        ExternalCompany company = dao.get(ourId);
//        if (company == null)
//            company = dao.get("ext_id=? and strCreator=?", ourId, Const.CREATOR_FIELD_VALUE);
//
//        return company;
//    }

    public ExternalPerson getExternalPerson (long id) throws SQLException {
        return runAction(transaction -> transaction.dao(ExternalPerson.class).get(id));
    }

    public <R> R runAction (LegacyDAO_Action<R> action) throws SQLException {
        try (LegacyDAO_Transaction daoFactory = new LegacyDAO_Transaction_impl(connProvider)){
            return action.doAction(daoFactory);
        }
    }

    public interface LegacyEntityDAO<T extends LegacyEntity> {
        boolean exists (Long id) throws SQLException;
        boolean exists (String cond, Object...args) throws SQLException;

//        /**
//         * Пытаемся сначала получить объект по id, потом по совпадению пармы ext_id + creator
//         * @param id
//         * @param <T>
//         * @return
//         * @throws SQLException
//         */
//        T findExportEntry (long id) throws SQLException;

        T get (Long id) throws SQLException;
        T get (String cond, Object...args) throws SQLException;
        List<T> list (String cond, Object...args) throws SQLException;

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

//        @Override
//        public T findExportEntry(long id) throws SQLException {
//            T val = get(id);
//            if (val == null) {
//                val = get ("ext_id=? and strCreator=?", id, Const.CREATOR_FIELD_VALUE);
//            }
//            return val;
//        }

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
