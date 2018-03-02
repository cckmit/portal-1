package ru.protei.portal.tools.migrate;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.tools.migrate.imp.ImportDataService;
import ru.protei.portal.tools.migrate.utils.MigrateAction;
import ru.protei.portal.tools.migrate.sybase.SybConnProvider;

import java.sql.Connection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by michael on 26.07.16.
 */
@Component
public class MigrationRunner {

    private static Logger logger = Logger.getLogger(MigrationRunner.class);

    private static Lock runLock;

    @Autowired
    ImportDataService importDataService;

    @Autowired
    PortalConfig config;

    static {
        runLock = new ReentrantLock();
    }

    public MigrationRunner() {

    }

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 30000)
    public void runMigrate() {
        if (!config.data().legacySysConfig().isImportEmployeesEnabled())
            return;

        if (runLock.tryLock()) {
            logger.info("Start migration process in scheduled mode");
            importDataService.incrementalImport();
            logger.info("Import completed, wait till next time");
            runLock.unlock();
        } else {
            logger.info("attempt to run migration concurrently detected, skip and return");
        }
    }
}
