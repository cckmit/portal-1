package ru.protei.portal.core.service.autoopencase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.CaseService;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

/**
 * Сервис выполняющий автоокрытие помеченных кейсов через определенное время
 */
public class AutoOpenCaseServiceImpl implements AutoOpenCaseService {

    @Async(BACKGROUND_TASKS)
    @Override
    public void scheduleCaseOpen() {
        log.info("Schedule case open at startup");
        caseObjectDAO.getCaseIdToAutoOpen()
                .forEach(caseId -> createTask(caseId, TimeUnit.MINUTES.toMillis(1) + makeRandomDelaySecond(120)));
    }

    @Override
    public void processNewCreatedCaseToAutoOpen(Long caseId, Long companyId) {
        Company company = companyDAO.get(companyId);
        if (company.getAutoOpenIssue() != null && company.getAutoOpenIssue()) {
            createTask(caseId, TimeUnit.MINUTES.toMillis(3) + makeRandomDelaySecond(120));
        }
    }

    @Override
    public ScheduledFuture<?> createTask(Long caseId, long delay) {
        log.info("Schedule case open id = {}, delay = {}", caseId, delay);
        return scheduler.schedule(() -> openCaseHandler.runOpenCaseTask(caseId), new Date(new Date().getTime() + delay));
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
    ThreadPoolTaskScheduler scheduler;


    private final Random random = new Random();

    private static Logger log = LoggerFactory.getLogger( AutoOpenCaseService.class );
}
