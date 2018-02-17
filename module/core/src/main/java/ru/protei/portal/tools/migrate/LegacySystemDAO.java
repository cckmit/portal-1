package ru.protei.portal.tools.migrate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import protei.sql.Tm_SqlHelper;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;
import ru.protei.portal.tools.migrate.struct.ExternalPersonExtension;
import ru.protei.portal.tools.migrate.struct.ExternalProduct;
import ru.protei.portal.tools.migrate.sybase.SybConnProvider;

import java.net.UnknownHostException;
import java.sql.*;

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
        try (Connection connection = connProvider.getConnection()) {
            return Tm_SqlHelper.getObjectEx(connection, ExternalProduct.class, id);
        }
    }

    public ExternalCompany getExternalCompany (long id) throws SQLException {
        try (Connection connection = connProvider.getConnection()) {
            return Tm_SqlHelper.getObjectEx(connection, ExternalCompany.class, id);
        }
    }

    public ExternalPerson getExternalPerson (long id) throws SQLException {
        try (Connection connection = connProvider.getConnection()){
            return Tm_SqlHelper.getObjectEx(connection, ExternalPerson.class, id);
        }
    }
}
