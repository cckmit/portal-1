package ru.protei.portal.redmine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;

import javax.annotation.PostConstruct;

/**
 * Сервис выполняющий первичную инициализацию, работу с исправлением данных
 */
public class BootstrapService {

    private static Logger logger = LoggerFactory.getLogger(BootstrapService.class);

    @PostConstruct
    public void init() {
        updateCreationDateAttachments();
        updateNullIssueCreator();
    }

    private void updateCreationDateAttachments() {
        /* for one-time only before integration.redmine=true */
        if (portalConfig.data().integrationConfig().isRedmineUpdateCreationDateAttachmentsEnabled()) {
            logger.debug("Update creation date of issue attachments started");
            redmineEndpointDAO.getAll().forEach(redmineService::updateCreationDateAttachments);
            logger.debug("Update creation date of issue attachments ended");
        }
    }

    private void updateNullIssueCreator() {
        if (portalConfig.data().integrationConfig().isRedmineEnabled()) {
            logger.debug("Update null issue creator started");
            boolean result = caseObjectDAO.updateNullCreatorByExtAppType("redmine");
            logger.debug("Update null issue creator ended with result {}", result);
        }
    }

    @Autowired
    PortalConfig portalConfig;
    @Autowired
    RedmineEndpointDAO redmineEndpointDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    private RedmineService redmineService;
}
