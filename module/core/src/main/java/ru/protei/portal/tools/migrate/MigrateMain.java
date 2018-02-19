package ru.protei.portal.tools.migrate;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.tools.migrate.imp.ImportDataService;
import ru.protei.portal.tools.migrate.utils.MigrateAction;
import ru.protei.portal.tools.migrate.sybase.SybConnProvider;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.sql.Connection;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateMain {


    public static void main (String argv[]){

//        Connection conn_src = null;
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class
        );

        try {
            ctx.getBean(ImportDataService.class).importInitialData();
        }
        catch (Throwable e) {
            e.printStackTrace(System.err);
        }

    }
}
