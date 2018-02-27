package ru.protei.portal.test.legacy;

import protei.sql.Tm_SqlHelper;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

public class ConnTest {

    public static void main(String argv[]) {

        String connUrl = "jdbc:jtds:sybase://192.168.1.55:2638/PORTAL2017";

        try {
            DriverManager.registerDriver((Driver) Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance());
            Properties properties = new Properties();
            properties.put("USER", "dba");
            properties.put("PASSWORD", "sql");

            Connection conn = DriverManager.getConnection(connUrl, properties);

            System.out.println(conn);

            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            ResultSet rs = null;

//            rs = conn.createStatement().executeQuery("select * from \"Resource\".\"Tm_Person\" where nID<100");
//
//            while (rs.next()) {
//                ExternalPerson ext = Tm_SqlHelper.getObjectFromRS(ExternalPerson.class, rs);
////                System.out.println(rs.getString("strLastName"));
//                System.out.println(ext);
//            }

            System.out.println(Tm_SqlHelper.getObjectEx(conn, ExternalPerson.class, 18L));


//            rs = conn.createStatement().executeQuery("select * from \"CRM\".\"VIEW_Session\" where nID<100");
//
//            while (rs.next())
//                System.out.println(rs.getString("strProduct") + "/" + rs.getString("strDescription"));

            conn.close();
        } catch (Throwable e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

}
