package ru.protei.portal.tools.migrate.sybase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import protei.sql.Tm_SqlHelper;
import protei.sql.metadata.MetaData;
import protei.sql.query.IQuery;
import protei.sql.query.IQueryCmd;
import protei.sql.query.Tm_BaseQueryCmd;
import protei.sql.utils.Tm_QueryExecutor;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.LegacyEntity;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.tools.migrate.Const;
import ru.protei.portal.tools.migrate.struct.*;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
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


    public Map<Long, Long> getSession2ContactMap (Long minId, Long maxId) {
        Connection connection = null;
        try {
            connection = connProvider.getConnection();
            return getSession2ContactMap(connection, minId, maxId);
        }
        catch (SQLException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
        finally {
            Tm_SqlHelper.safeCloseConnection(connection, false);
        }
    }


    public List<Long> getSessionContactList (Connection connection, Long sessionId) {
        try (PreparedStatement p = connection.prepareStatement(
                "select nContactID from CRM.Tm_ContactToSession where nSessionID=? order by nID"
        )) {
            p.setLong(1, sessionId);
            ResultSet rs = p.executeQuery();
            List<Long> result = new ArrayList<>();

            while (rs.next()) {
                result.add(rs.getLong("nContactID"));
            }
            rs.close();
            return result;
        }
        catch (SQLException e) {
            logger.debug("",e);
            throw new RuntimeException(e);
        }
    }

    private Map<Long, Long> getSession2ContactMap (Connection conn, Long minId, Long maxId) {

        try (PreparedStatement p = conn.prepareStatement(
                "select nSessionID, nContactID from CRM.Tm_ContactToSession where nSessionID between ? and ? order by nID"
        )) {
            p.setLong(1, minId);
            p.setLong(2, maxId);

            ResultSet rs = p.executeQuery();

            Map<Long,Long> result = new HashMap<>();

            while (rs.next()) {
                result.putIfAbsent(rs.getLong("nSessionID"), rs.getLong("nContactID"));
            }

            rs.close();

            return result;
        }
        catch (SQLException e) {
            logger.debug("",e);
            throw new RuntimeException(e);
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

    public En_ResultStatus saveExternalEmployee(Person person, String departmentName, String positionName) {
        if (person == null || person.getId () == null) return En_ResultStatus.INCORRECT_PARAMS;

        return runActionNE(transaction -> {
            ExternalPerson externalPerson = transaction.dao(ExternalPerson.class).get(person.getOldId());

            if (externalPerson != null) {
                logger.debug ("mergeEmployee(): person={}", person);

                externalPerson.updateContactFrom(person);
                externalPerson.setDepartment(departmentName);
                externalPerson.setPosition(positionName);
                externalPerson.setCompanyId(1L);

                ExternalPersonExtension externalPersonExtension = transaction.dao(ExternalPersonExtension.class).get(externalPerson.getId());
                if (externalPersonExtension == null) {
                    externalPersonExtension = new ExternalPersonExtension(person);
                    externalPersonExtension.setId(externalPerson.getId());
                }
                else
                    externalPersonExtension.updateFromPerson(person);

                externalPersonExtension.setPersonId(externalPerson.getId());

                transaction.dao(ExternalPerson.class).update(externalPerson);
                transaction.dao(ExternalPersonExtension.class).saveOrUpdate(externalPersonExtension);

                transaction.commit();
            }
            else {
                logger.debug ("persistEmployee(): person={}", person);

                externalPerson = new ExternalPerson(person, departmentName, positionName, ourHost);
                externalPerson.setCompanyId(1L);
                transaction.dao(ExternalPerson.class).insert(externalPerson);

                ExternalPersonExtension externalPersonExtension = new ExternalPersonExtension(person);
                externalPersonExtension.setId(externalPerson.getId());
                externalPersonExtension.setPersonId(externalPerson.getId());
                transaction.dao(ExternalPersonExtension.class).insert(externalPersonExtension);

                transaction.commit();

                person.setOldId(externalPerson.getId());
                personDAO.partialMerge(person, "old_id");
            }

            return En_ResultStatus.OK;
        }, En_ResultStatus.DB_COMMON_ERROR);
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

    public List<ExternalSubnet> getExternalSubnets () throws SQLException {
        return runAction(transaction -> transaction.dao(ExternalSubnet.class).list());
    }

    public List<ExternalReservedIp> getExternalReservedIps () throws SQLException {
        return runAction(transaction -> transaction.dao(ExternalReservedIp.class).list(""));
    }

    public List<ExternalPersonAbsence> getExternalAbsences (String date) throws SQLException {
        return runAction(transaction -> transaction.dao(ExternalPersonAbsence.class).list("dToDate >= ? ", date));
    }

    public List<ExternalPersonLeave> getExternalLeaves (String date) throws SQLException {
        return runAction(transaction -> transaction.dao(ExternalPersonLeave.class).list("dToDate >= ? ", date));
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
            this.personMap = HelperFunc.map(personList, person -> person.getId());
            this.keys = new ArrayList<>(personMap.keySet());
            return this;
        }

        ExternalPersonInfoCollector fromExtSet (List<ExternalPersonExtension> extensionList) {
            this.extensionMap = HelperFunc.map(extensionList, ext -> ext.getPersonId());
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
                personMap = HelperFunc.map(transaction.dao(ExternalPerson.class).list(keys), person -> person.getId());
            }

            if (extensionMap == null) {
                extensionMap = HelperFunc.map(transaction.dao(ExternalPersonExtension.class).list(keys), ext -> ext.getPersonId());
            }

            final Map<Long, ExternalPersonInfo> tmp = new HashMap<>();
            keys.forEach(id -> tmp.put(id, new ExternalPersonInfo(personMap.get(id), extensionMap.get(id))));

            transaction.dao(ExtContactProperty.class)
                    .list("nPersonID", keys)
                    .forEach(cont -> tmp.get(cont.personId).addContactData(cont));

            return tmp;
        }
    }

    public List<ExtCrmComment> getCrmComments (Long sessionLowId, Long sessionHighId) {
        Connection connection = null;

        ResultSet resultSet = null;

        try {
            connection = connProvider.getConnection();
            IQuery commentQuery = new LegacyQuery("select c.* from CRM.Tm_SessionComment c" +
                    " join CRM.Tm_Session s on (s.nID=c.nSessionID)" +
                    " where s.nID between ? and ? and s.nCategoryID=? order by c.nID", sessionLowId, sessionHighId, 8);

            resultSet = Tm_QueryExecutor.execQueryFWD(commentQuery, -1, connection);

            return Tm_SqlHelper.getObjectsList(ExtCrmComment.class, resultSet);
        }
        catch (SQLException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
        finally {
            //Tm_SqlHelper.safeCloseResultSet(resultSet, false);
            try { resultSet.getStatement().close(); resultSet.close(); } catch (Throwable e) {}
            Tm_SqlHelper.safeCloseConnection(connection, false);
        }
    }


    public <R> R runAction (LegacyDAO_Action<R> action) throws SQLException {
        try (LegacyDAO_Transaction transaction = new LegacyDAO_Transaction_impl(connProvider)){
            return action.doAction(transaction);
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

    public <R> R runActionNE (LegacyDAO_Action<R> action, R ERR_RESULT) {
        try {
            return runAction (action);
        }
        catch (SQLException e) {
            logger.error("sql-error (action)", e);
            return ERR_RESULT;
        }
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
        public Connection connection() {
            return connection;
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
        public List<T> list(int top) throws SQLException {
            MetaData<T> metaData = Tm_SqlHelper.getMetaData(entityType);
            String sql = "select top " + top + " " + metaData.selectColumns + " from " + metaData.table.getName();
            LegacyQuery query = new LegacyQuery(sql, null);
            return Tm_SqlHelper.getObjectsListEx(connection, entityType, query);
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

        @Override
        public void delete(String condition, Object... args) throws SQLException {
            Tm_SqlHelper.deleteObjectEx(connection, entityType, condition, args);
        }

        @Override
        public void delete(List<T> entities) throws SQLException {
            Tm_SqlHelper.deleteObjectListEx(connection,entityType,entities);
        }
    }


    public static class LegacyQuery implements IQuery {

        private IQueryCmd command;

        public LegacyQuery(String query, Object...args) {
            Tm_BaseQueryCmd cmd = new Tm_BaseQueryCmd();
            cmd.setCommad(query);
            if (args != null && args.length > 0)
                cmd.addParamAll(Arrays.asList(args));
            this.command = cmd;
        }

        @Override
        public String getName() {
            return "legacy-query";
        }

        @Override
        public void setName(String name) {

        }

        @Override
        public int getMaxRows() {
            return -1;
        }

        @Override
        public void setMaxRows(int nMaxRows) {

        }

        @Override
        public int getFetchSize() {
            return 500;
        }

        @Override
        public void setFetchSize(int nSize) {

        }

        @Override
        public IQueryCmd getQueryCmd() throws SQLException {
            return command;
        }

        @Override
        public void reset() {

        }
    }
}
