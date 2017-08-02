package ru.protei.portal.tools.migrate;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.tools.migrate.tools.MigrateAction;
import ru.protei.portal.tools.notifications.NotificationConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateMain {


    public static void main (String argv[]){

        Connection conn_src = null;

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class,
                MigrateConfiguration.class
        );


        MigrateSetup setup = ctx.getBean(MigrateSetup.class);
        try {
            DriverManager.registerDriver(new com.sybase.jdbc3.jdbc.SybDriver());
            conn_src = DriverManager.getConnection("jdbc:sybase:Tds:192.168.101.140:2638/RESV3", "dba", "sql");

            for (MigrateAction a : setup.sortedList()) {
                a.migrate(conn_src);
            }
        }
        catch (Throwable e) {
            //System.out.println(e.);
            e.printStackTrace(System.err);
        }

    }
}
