package ru.protei.portal.test.legacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnTestNative {

    public static void main(String argv[]) {

        String connUrl = "jdbc:sybase:Tds:192.168.1.55:2638/PORTAL2017";

        try {
            Class.forName("com.sybase.jdbc3.jdbc.SybDriver").newInstance();

            Connection conn = DriverManager.getConnection(connUrl, "dba", "sql");

            System.out.println(conn);

            conn.close();
        } catch (Throwable e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

}
