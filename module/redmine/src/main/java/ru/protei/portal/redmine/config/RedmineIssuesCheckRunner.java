package ru.protei.portal.redmine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.redmine.service.RedmineService;

@Component
public final class RedmineIssuesCheckRunner {

    public RedmineIssuesCheckRunner() {
        logger.debug("Redmine issues checker created");
    }

    @Scheduled(fixedRate = SCHEDULE_TIME)
    public void queryIssues() {
        logger.debug("Check for new issues stared");
        redmineEndpointDAO.getAll().forEach(redmineService::checkForNewIssues);
        logger.debug("Check for new issues ended");

        logger.debug("Check for issues updates started");
        redmineEndpointDAO.getAll().forEach(redmineService::checkForUpdatedIssues);
        logger.debug("Check for issues updates ended");
    }

    private static final Logger logger = LoggerFactory.getLogger(RedmineIssuesCheckRunner.class);

    //5 minutes in MS
    private static final long SCHEDULE_TIME = 5 * 60 * 1000L;

    @Autowired
    private RedmineService redmineService;

    @Autowired
    private RedmineEndpointDAO redmineEndpointDAO;
}
