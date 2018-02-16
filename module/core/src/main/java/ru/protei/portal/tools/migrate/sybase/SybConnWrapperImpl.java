package ru.protei.portal.tools.migrate.sybase;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SybConnWrapperImpl implements SybConnProvider {

//    private String jdbcURL;
//    private String user;
//    private String pwd;
    PoolDataSource pds;

    public SybConnWrapperImpl(String jdbcURL, String user, String pwd) throws SQLException {
//        this.jdbcURL = jdbcURL;
//        this.user = user;
//        this.pwd = pwd;
//
//        DriverManager.registerDriver(new com.sybase.jdbc3.jdbc.SybDriver());

        pds = PoolDataSourceFactory.getPoolDataSource();
        pds.setConnectionPoolName("legacy-db");
        pds.setConnectionFactoryClassName("com.sybase.jdbc3.jdbc.SybDriver");
        pds.setURL(jdbcURL);
        pds.setUser(user);
        pds.setPassword(pwd);

//        if (config.getTestQuery() != null) {
//            pds.setValidateConnectionOnBorrow(true);
//            pds.setSQLForValidateConnection(config.getTestQuery());
//        }

        pds.setMinPoolSize(1);
        pds.setMaxPoolSize(5);
        pds.setMaxConnectionReuseTime(600);
        pds.setConnectionWaitTimeout(5);
        pds.setInactiveConnectionTimeout(120);
        pds.setTimeoutCheckInterval(60);


        Properties props = new Properties();

        props.setProperty("password", pwd);
        props.setProperty("user", user);
        pds.setConnectionProperties(props);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return pds.getConnection(); //DriverManager.getConnection(jdbcURL, user, pwd);
    }
}
