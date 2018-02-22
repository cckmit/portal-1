package ru.protei.portal.redmine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.redmine.service.RedmineService;

@Component
public class RedmineIssuesCheckRunner {
    private static final Logger logger = LoggerFactory.getLogger(RedmineIssuesCheckRunner.class);

    @Autowired
    private RedmineService redmineService;

    @Autowired
    private RedmineEndpointDAO redmineEndpointDAO;

    public RedmineIssuesCheckRunner() {
        logger.debug("Redmine new issues checker created");
    }

    //Every 5 mins
    @Scheduled(fixedRate = 31 * 1000)
    public void queryNewIssues() {
        logger.debug("Check for new issues stared");
        redmineEndpointDAO.getAll().forEach(redmineService::checkForNewIssues);
        logger.debug("Check for new issues ended");
    }

    //Every 5 mins
    @Scheduled(fixedRate = 29 * 1000)
    public void queryIssuesUpdates() {
        logger.debug("Check for issues updates started");
        logger.debug("Check for issues updates ended");
    }
}
