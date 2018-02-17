package ru.protei.portal.tools.migrate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import protei.sql.Tm_SqlHelper;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.tools.migrate.struct.*;
import ru.protei.portal.tools.migrate.sybase.SybConnProvider;

import java.io.Closeable;
import java.io.IOException;
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
            ExternalPerson externalPerson = new ExternalPerson (person, "", "");
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
            ExternalPerson externalPerson = new ExternalPerson(person, departmentName, positionName);
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

            ExternalPerson externalPerson = new ExternalPerson (person, departmentName, positionName);
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
        return runAction(factory -> factory.dao(ExternalProduct.class).get(id));
    }

    public ExternalCompany getExternalCompany (long id) throws SQLException {
        return runAction(factory -> factory.dao(ExternalCompany.class).get(id));
    }

    public ExternalPerson getExternalPerson (long id) throws SQLException {
        return runAction(factory -> factory.dao(ExternalPerson.class).get(id));
    }

    public <R> R runAction (LegacyDAO_Action<R> action) throws SQLException {
        try (LegacyEntityDAO_Factory daoFactory = new LegacyEntityDAO_Factory_impl(connProvider)){
            return action.doAction(daoFactory);
        }
    }

    interface LegacyEntityDAO<T extends LegacyEntity> {
        boolean exists (long id) throws SQLException;
        boolean exists (String cond, Object...args) throws SQLException;

        T get (long id) throws SQLException;
        T get (String cond, Object...args) throws SQLException;
        List<T> list (String cond, Object...args) throws SQLException;

        T insert (T entity) throws SQLException;
        T update (T entity) throws SQLException;

        default T store (T entity) throws SQLException {
            return (exists(entity.getId())) ? update(entity) : insert(entity);
        }

        T delete (T entity) throws SQLException;
        void delete (long id) throws SQLException;
    }

    interface LegacyEntityDAO_Factory extends AutoCloseable {
        <T extends LegacyEntity> LegacyEntityDAO<T> dao(Class<T> type) throws SQLException;
        void close();
    }

    interface LegacyDAO_Action<R> {
        R doAction (LegacyEntityDAO_Factory factory) throws SQLException;
    }

    static class LegacyEntityDAO_Factory_impl implements LegacyEntityDAO_Factory {
        private Connection connection;
        private Map<Class<?>, LegacyEntityDAO<?>> daoMap;

        public LegacyEntityDAO_Factory_impl(SybConnProvider provider) throws SQLException {
            this.connection = provider.getConnection();
            this.daoMap = new HashMap<>();
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
        public boolean exists(long id) throws SQLException {
            return Tm_SqlHelper.exists(connection, entityType, id);
        }

        @Override
        public boolean exists(String cond, Object... args) throws SQLException {
            return Tm_SqlHelper.exists(connection, entityType, cond, args);
        }

        @Override
        public T get(long id) throws SQLException {
            return Tm_SqlHelper.getObjectEx(connection, entityType, id);
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
        public void delete(long id) throws SQLException {
            Tm_SqlHelper.deleteObject(connection, entityType, id);
        }
    }
}
