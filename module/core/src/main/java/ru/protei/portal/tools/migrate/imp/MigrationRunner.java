package ru.protei.portal.tools.migrate.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.protei.portal.config.PortalConfig;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by michael on 26.07.16.
 */
public class MigrationRunner {

    private static Logger logger = LoggerFactory.getLogger(MigrationRunner.class);

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

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 60000)
    public void runMigrate() {
        if (!config.data().legacySysConfig().isImportEnabled())
            return;

        if (runLock.tryLock()) {
            logger.info("Start migration process in scheduled mode");
            importDataService.runIncrementalImport();
            logger.info("Import completed, wait till next time");
            runLock.unlock();
        } else {
            logger.info("attempt to run migration concurrently detected, skip and return");
        }
    }
}
