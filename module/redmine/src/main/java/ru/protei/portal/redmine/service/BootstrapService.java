package ru.protei.portal.redmine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;

import javax.annotation.PostConstruct;

/**
 * Сервис выполняющий первичную инициализацию, работу с исправлением данных
 */
public class BootstrapService {

    private static Logger logger = LoggerFactory.getLogger(BootstrapService.class);

    @PostConstruct
    public void init() {
        updateIssueCreatorAndCreationDateAttachment();
    }

    private void updateIssueCreatorAndCreationDateAttachment() {
        if (!portalConfig.data().integrationConfig().isRedmineEnabled()) {
            logger.debug("Redmine integration is disabled in config, therefore nothing happens");
            return;
        }

        logger.debug("Update issue creator and creation date of attachments started");
        redmineEndpointDAO.getAll().forEach(redmineService::updateIssueCreatorAndCreationDateAttachment);
        logger.debug("Update issue creator and creation date of attachments ended");
    }

    @Autowired
    PortalConfig portalConfig;
    @Autowired
    RedmineEndpointDAO redmineEndpointDAO;
    @Autowired
    private RedmineService redmineService;
}
