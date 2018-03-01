package ru.protei.portal.test.legacy;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

public class ConnTestNative {

    public static void main(String argv[]) {

        String connUrl = "jdbc:sybase:Tds:192.168.1.55:2638/PORTAL2017";

        try {
            Class.forName("com.sybase.jdbc3.jdbc.SybDriver");

            Connection conn = DriverManager.getConnection(connUrl, "dba", "sql");

            System.out.println(conn);
            System.out.println(conn.getMetaData().getDatabaseProductName());
            System.out.println(conn.getMetaData().supportsGetGeneratedKeys());

            ResultSet rs = conn.createStatement().executeQuery("select * from \"Resource\".Tm_Person where nID=18");
            rs.next();

            System.out.println(rs.getString("strCreator").length());

            conn.close();
        } catch (Throwable e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

}
