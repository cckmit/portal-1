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
    private static final Logger logger = LoggerFactory.getLogger(RedmineIssuesCheckRunner.class);

    @Autowired
    private RedmineService redmineService;

    @Autowired
    private RedmineEndpointDAO redmineEndpointDAO;

    public RedmineIssuesCheckRunner() {
        logger.debug("Redmine new issues checker created");
    }

    /*
        @review А зачем нужно разделять процесс получения новых issue и update по имеющимся?
        Мне видится, что это нужно сделать в один заход, ни к чему это разделять.
        Проще же будет видеть процесс по логам.
        И зачем такие странные числа в качестве периода запуска?
     */

    //Every 5 mins (well, actually, it is not exactly 5 mins, but who really cares?)
    @Scheduled(fixedRate = 5 * 59 * 1000)
    public void queryNewIssues() {
        logger.debug("Check for new issues stared");
        redmineEndpointDAO.getAll().forEach(redmineService::checkForNewIssues);
        logger.debug("Check for new issues ended");
    }

    //Every 5 mins (well, actually, it is not exactly 5 mins, but who really cares?)
    @Scheduled(fixedRate = 5 * 63 * 1000)
    public void queryIssuesUpdates() {
        logger.debug("Check for issues updates started");
        redmineEndpointDAO.getAll().forEach(redmineService::checkForIssuesUpdates);
        logger.debug("Check for issues updates ended");
    }
}
