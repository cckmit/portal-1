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
            return threadPool.submit(() -> openCaseHandler.runOpenCaseTask(caseId));
        } else {
            return threadPool.schedule(() -> openCaseHandler.runOpenCaseTask(caseId), new Date(new Date().getTime() + delay));
        }
    }

    private long makeDelay(boolean isStartup) {
        PortalConfigData.AutoOpenConfig autoOpenConfig = portalConfig.data().getAutoOpenConfig();
        if (autoOpenConfig.getEnableDelay()) {
            return isStartup ? TimeUnit.SECONDS.toMillis(portalConfig.data().getAutoOpenConfig().getDelayStartup()) :
                               TimeUnit.SECONDS.toMillis(portalConfig.data().getAutoOpenConfig().getDelayRuntime())
                                        + makeRandomDelaySecond(portalConfig.data().getAutoOpenConfig().getDelayRandom());
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
