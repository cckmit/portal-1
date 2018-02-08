package ru.protei.portal.redmine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.protei.portal.redmine.service.RedmineService;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedmineIssuesCheckRunner {
    private static final Logger logger = LoggerFactory.getLogger(RedmineIssuesCheckRunner.class);

    @Autowired
    private RedmineService redmineService;

    private final List<RedmineProjectConfig> configList;

    public RedmineIssuesCheckRunner() {
        logger.debug("Redmine new issues checker has started");
        configList = new ArrayList<>();
    }

    @Scheduled(fixedRate = 30 * 60000)
    public void queryNewIssues() {
        logger.debug("Check for new issues stared");
        configList.forEach(redmineService::checkForNewIssues);
        logger.debug("Check for new issues ended");
    }

    @Scheduled(fixedRate = 31 * 60000)
    public void queryIssuesUpdates() {
        logger.debug("Check for issues updates started");
        redmineService.checkForIssuesUpdates();
        logger.debug("Check for issues updates ended");
    }
}
