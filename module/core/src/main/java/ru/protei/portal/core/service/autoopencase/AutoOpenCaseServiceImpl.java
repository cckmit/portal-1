package ru.protei.portal.core.service.autoopencase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.config.PortalConfigData;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.service.CaseService;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.util.CrmConstants.AutoOpen.NO_DELAY;

/**
 * Сервис выполняющий автоокрытие помеченных кейсов через определенное время
 */
public class AutoOpenCaseServiceImpl implements AutoOpenCaseService {

    @Async(BACKGROUND_TASKS)
    @Override
    public void scheduleCaseOpen() {
        log.info("Schedule case open at startup");
        if (!portalConfig.data().getAutoOpenConfig().getEnable()) {
            log.debug("Case open is disabled in config");
            return;
        }
        caseObjectDAO.getCaseIdToAutoOpen()
                .forEach(caseId -> createTask(caseId, makeDelay(true)));
    }

    @Override
    public void processNewCreatedCaseToAutoOpen(Long caseId, Long companyId) {
        if (!portalConfig.data().getAutoOpenConfig().getEnable()) {
            log.debug("Case open is disabled in config");
            return;
        }
        Company company = companyDAO.get(companyId);
        if (company.getAutoOpenIssue() != null && company.getAutoOpenIssue()) {
            createTask(caseId, makeDelay(false));
        }
    }

    @Override
    public Future<?> createTask(Long caseId, long delay) {
        log.info("Schedule case open id = {}, delay = {}", caseId, delay);
        if (delay == NO_DELAY) {
            openCaseHandler.runOpenCaseTask(caseId);
            return new CompletableFuture<Void>();
        } else {
            return threadPool.schedule(() -> openCaseHandler.runOpenCaseTask(caseId), new Date(new Date().getTime() + delay));
        }
    }

    private long makeDelay(boolean isStartup) {
        PortalConfigData.AutoOpenConfig autoOpenConfig = portalConfig.data().getAutoOpenConfig();
        Integer delayStartup;
        Integer delayRuntime;
        Integer delayRandom;
        if (autoOpenConfig.getDelayStartup() >= 0) {
            delayStartup = portalConfig.data().getAutoOpenConfig().getDelayStartup();
        } else {
            log.error("makeDelay: config delayStartup ={} not >= 0, set delayStartup =0", autoOpenConfig.getDelayStartup());
            delayStartup = 0;
        }

        if (autoOpenConfig.getDelayRuntime() >= 0) {
            delayRuntime = portalConfig.data().getAutoOpenConfig().getDelayRuntime();
        } else {
            log.error("makeDelay: config delayRuntime ={} not >= 0, set delayRuntime =0", autoOpenConfig.getDelayRuntime());
            delayRuntime = 0;
        }

        if (autoOpenConfig.getDelayRandom() >= 1) {
            delayRandom = portalConfig.data().getAutoOpenConfig().getDelayRandom();
        } else {
            log.error("makeDelay: config delayRandom ={} not >= 1, set delayRandom =1", autoOpenConfig.getDelayRuntime());
            delayRandom = 1;
        }

        if (autoOpenConfig.getEnableDelay()) {
            return isStartup ? TimeUnit.SECONDS.toMillis(delayStartup) : TimeUnit.SECONDS.toMillis(delayRuntime)
                                        + makeRandomDelaySecond(delayRandom);
        } else {
            return NO_DELAY;
        }
    }

    private long makeRandomDelaySecond(int bound) {
        return TimeUnit.SECONDS.toMillis(random.nextInt(bound));
    }

    @Autowired
    CaseService caseService;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    AutoOpenCaseTaskHandler openCaseHandler;

    @Autowired
    ThreadPoolTaskScheduler threadPool;

    @Autowired
    PortalConfig portalConfig;

    private final Random random = new Random();

    private static Logger log = LoggerFactory.getLogger( AutoOpenCaseService.class );
}
