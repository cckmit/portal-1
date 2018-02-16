package ru.protei.portal.tools.migrate;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.protei.portal.tools.migrate.tools.MigrateAction;
import ru.protei.portal.tools.migrate.sybase.SybConnProvider;

import java.sql.Connection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by michael on 26.07.16.
 */
@Component
public class MigrationRunner {

//    public static final String PORTAL_SYBASE_JDBC_URL = "jdbc:sybase:Tds:192.168.101.140:2638/RESV3";

    private static Logger logger = Logger.getLogger(MigrationRunner.class);

    private static Lock runLock;

    @Autowired
    MigrateSetup setup;

    @Autowired
    SybConnProvider connProvider;

    static {
//        try {
//            DriverManager.registerDriver(new com.sybase.jdbc3.jdbc.SybDriver());
//        } catch (SQLException e) {
//            logger.error("unable to init Sybase driver", e);
//        }

        runLock = new ReentrantLock();
    }

    public MigrationRunner() {

    }

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 10000)
    public void runMigrate() {
        if (runLock.tryLock()) {
            doMigrate();
            runLock.unlock();
        } else {
            logger.info("attempt to run migration concurrently detected, skip and return");
        }
    }

    private void doMigrate() {
        try (Connection conn_src = connProvider.getConnection()) {

            logger.info("Start migration process in scheduled mode");

            for (MigrateAction a : setup.sortedList()) {
                a.migrate(conn_src);
            }

            logger.info("Migration executed, wait till next time");

        } catch (Throwable e) {
            logger.error("Unable to perform migration", e);
        }
    }
}
