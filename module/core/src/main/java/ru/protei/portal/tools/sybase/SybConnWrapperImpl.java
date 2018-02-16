package ru.protei.portal.tools.sybase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SybConnWrapperImpl implements SybConnProvider {

    private String jdbcURL;
    private String user;
    private String pwd;

    public SybConnWrapperImpl(String jdbcURL, String user, String pwd) throws SQLException {
        this.jdbcURL = jdbcURL;
        this.user = user;
        this.pwd = pwd;

        DriverManager.registerDriver(new com.sybase.jdbc3.jdbc.SybDriver());
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, user, pwd);
    }
}
