package ru.protei.portal.cli;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.tools.migrate.imp.ImportDataService;
import ru.protei.portal.tools.migrate.utils.MigrateUtils;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateMain {


    public static void main (String argv[]){

//        Connection conn_src = null;
        if (MigrateUtils.getMail2LoginRules().isEmpty()) {
            System.out.println("no migrate accounts config! (" + MigrateUtils.MIGRATE_ACCOUNTS_FIX_JSON +")");
            System.exit(1);
        }


        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class
        );

        try {
//            ctx.getBean(ImportDataService.class).importInitialCommonData();
//            ctx.getBean(ImportDataService.class).importInitialSupportSessions();
            ctx.getBean(ImportDataService.class).runIncrementalImport();
        }
        catch (Throwable e) {
            e.printStackTrace(System.err);
        }

    }
}