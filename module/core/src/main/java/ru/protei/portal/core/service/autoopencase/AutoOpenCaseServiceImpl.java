package ru.protei.portal.core.service.autoopencase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.CaseObjectCreateEvent;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.service.CaseService;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.util.CrmConstants.AutoOpen.*;

/**
 * Сервис выполняющий автоокрытие помеченных кейсов через определенное время
 */
public class AutoOpenCaseServiceImpl implements AutoOpenCaseService {

    @Async(BACKGROUND_TASKS)
    @Override
    public void scheduleCaseOpen() {
        log.info("Schedule case open at startup");
        caseObjectDAO.getCaseIdToAutoOpen()
                .forEach(caseId -> {
                    if (portalConfig.data().getAutoOpenConfig().getEnableDelay()) {
                        createTask(caseId, makeDelay(true));
                    } else {
                        performCaseOpen(caseId);
                    }
                });
    }

    @EventListener
    @Override
    public void onCaseObjectCreateEvent(CaseObjectCreateEvent event) {
        if (!portalConfig.data().getAutoOpenConfig().getEnable()) {
            log.debug("Case open is disabled in config");
            return;
        }

        log.info( "onCaseObjectCreateEvent(): CaseObjectId={}", event.getCaseObjectId() );
        Company company = companyDAO.get(event.getCaseObject().getInitiatorCompanyId());
        if (company.getAutoOpenIssue() != null && company.getAutoOpenIssue()) {
            if (portalConfig.data().getAutoOpenConfig().getEnableDelay()) {
                createTask(event.getCaseObjectId(), makeDelay(false));
            } else {
                performCaseOpen(event.getCaseObjectId());
            }
        }
    }

    @Override
    public Future<?> createTask(Long caseId, long delay) {
        log.info("Schedule case open id = {}, delay = {}", caseId, delay);
        return taskScheduler.schedule(() -> openCaseHandler.runOpenCaseTaskAsync(caseId), new Date(new Date().getTime() + delay));
    }

    @Override
    public void performCaseOpen(Long caseId) {
        openCaseHandler.runOpenCaseTask(caseId);
    }

    private long makeDelay(boolean isStartup) {
        return isStartup ? TimeUnit.SECONDS.toMillis(DELAY_STARTUP) : TimeUnit.SECONDS.toMillis(DELAY_RUNTIME)
                                + TimeUnit.SECONDS.toMillis(random.nextInt(DELAY_RANDOM));
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
    ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    PortalConfig portalConfig;

    private final Random random = new Random();

    private static Logger log = LoggerFactory.getLogger( AutoOpenCaseService.class );
}
