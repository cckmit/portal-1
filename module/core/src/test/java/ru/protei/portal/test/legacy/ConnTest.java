package ru.protei.portal.test.legacy;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnTest {

    public static void main(String argv[]) {

        String connUrl = "jdbc:jtds:sybase://192.168.1.55:2638/PORTAL2017";

        try {
            DriverManager.registerDriver((Driver) Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance());
            Properties properties = new Properties();
//            properties.put("SERVERNAME", "");
            properties.put("USER", "dba");
            properties.put("PASSWORD", "sql");
//            properties.put("TDS", "4.2");

            Connection conn = DriverManager.getConnection(connUrl, properties);

            System.out.println(conn);

/*            ResultSet rs = conn.createStatement().executeQuery("select * from \"Resource\".\"Tm_Person\" where nID<100");

            while (rs.next())
                System.out.println(rs.getString("strLastName"));


            rs = conn.createStatement().executeQuery("select * from \"CRM\".\"VIEW_Session\" where nID<100");

            while (rs.next())
                System.out.println(rs.getString("strProduct") + "/" + rs.getString("strDescription"));
*/
            conn.close();
        } catch (Throwable e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

}
