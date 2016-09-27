package ru.protei.portal.webui.controller.ws.tools.migrate;

import org.apache.log4j.Logger;
import protei.sql.Tm_SqlHelper;
import ru.protei.portal.core.model.ent.Person;

import java.net.UnknownHostException;
import java.sql.*;

/**
 * Created by turik on 08.09.16.
 */
public class WSMigrationManager {

    public static final String PORTAL_SYBASE_JDBC_URL = "jdbc:sybase:Tds:192.168.1.55:2638/RESV3";

    private static Logger logger = Logger.getLogger(WSMigrationManager.class);

    Connection conn_src;

    static {
        try {
            DriverManager.registerDriver (new com.sybase.jdbc3.jdbc.SybDriver ());
        } catch (SQLException e) {
            logger.error("unable to init Sybase driver", e);
        }
    }

    private Connection getConnection() throws SQLException {
        if (conn_src == null)
            conn_src = DriverManager.getConnection(PORTAL_SYBASE_JDBC_URL, "dba", "sql");

        return conn_src;
    }

    public void persistPerson(Person person) throws SQLException, UnknownHostException {

        logger.debug ("=== persistPerson ===");

        Connection connection = getConnection ();
        logger.debug ("=== connection = " + connection);

        PreparedStatement st = connection.prepareStatement("select * from " + Tm_SqlHelper.getTableName (ExternalPerson.class) + " where nID = " + person.getId ());
        ResultSet rs = st.executeQuery ();
        if (rs != null && rs.next()) {
            mergePerson (person);
        } else {
            ExternalPerson externalPerson = new ExternalPerson (person);
            String tableName = Tm_SqlHelper.getTableName (externalPerson.getClass ());
            String sql = Tm_SqlHelper.getInsertString (externalPerson.getClass (), false);
            logger.debug ("=== prepare statement = " + "insert into " + tableName + sql);
            st = connection.prepareStatement("insert into " + tableName + sql);
            Tm_SqlHelper.setParams(st, externalPerson, false);
            st.execute();
            st.close();
            connection.commit ();

            ExternalPersonExtension externalPersonExtension = new ExternalPersonExtension (person);
            tableName = Tm_SqlHelper.getTableName (externalPersonExtension.getClass ());
            sql = Tm_SqlHelper.getInsertString (externalPersonExtension.getClass (), false);
            logger.debug ("=== prepare statement = " + "insert into " + tableName + sql);
            st = connection.prepareStatement("insert into " + tableName + sql);
            Tm_SqlHelper.setParams(st, externalPersonExtension, false);
            st.execute();
            st.close();
            connection.commit ();
        }
    }

    public void mergePerson(Person person) throws SQLException, UnknownHostException {

        logger.debug ("=== mergePerson ===");

        Connection connection = getConnection ();
        logger.debug ("=== connection = " + connection);
        ExternalPerson externalPerson = new ExternalPerson (person);
        String tableName = Tm_SqlHelper.getTableName (ExternalPerson.class);
        String sql = Tm_SqlHelper.getUpdateString (externalPerson.getClass ());
        logger.debug ("=== prepare statement = " + "update " + tableName + " set " + sql + " where nID = " + person.getId ());
        PreparedStatement st = connection.prepareStatement("update " + tableName + " set " + sql + " where nID = " + person.getId ());
        Tm_SqlHelper.setParams(st, externalPerson, true);
        st.executeUpdate ();
        st.close();
        connection.commit ();

        ExternalPersonExtension externalPersonExtension = new ExternalPersonExtension (person);
        tableName = Tm_SqlHelper.getTableName (ExternalPersonExtension.class);
        sql = Tm_SqlHelper.getUpdateString (externalPersonExtension.getClass ());
        logger.debug ("=== prepare statement = " + "update " + tableName + " set " + sql + " where nID = " + person.getId ());
        st = connection.prepareStatement("update " + tableName + " set " + sql + " where nID = " + person.getId ());
        Tm_SqlHelper.setParams(st, externalPersonExtension, true);
        st.executeUpdate ();
        st.close();
        connection.commit ();
    }

    public void removePerson(Person person) throws SQLException, UnknownHostException {

        logger.debug ("=== removePerson ===");

        if (person != null && person.isDeleted ()) {
            Connection connection = getConnection ();
            logger.debug ("=== connection = " + connection);
            ExternalPerson externalPerson = new ExternalPerson (person);
            String tableName = Tm_SqlHelper.getTableName (ExternalPerson.class);
            String sql = Tm_SqlHelper.getUpdateString (externalPerson.getClass ());
            logger.debug ("=== prepare statement = " + "update " + tableName + " set " + sql + " where nID = " + person.getId ());
            PreparedStatement st = connection.prepareStatement("update " + tableName + " set " + sql + " where nID = " + person.getId ());
            Tm_SqlHelper.setParams(st, externalPerson, true);
            st.executeUpdate ();
            st.close();
            connection.commit ();
        }
    }
}
