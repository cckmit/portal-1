package ru.protei.portal.redmine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.RedmineEndpointDAO;
import ru.protei.portal.core.model.dict.En_ExtAppType;

import javax.annotation.PostConstruct;

/**
 * Сервис выполняющий первичную инициализацию, работу с исправлением данных
 */
public class RedmineBootstrapService {

    private static Logger logger = LoggerFactory.getLogger(RedmineBootstrapService.class);

    @PostConstruct
    public void init() {
        //updateCreationDateAttachments();
        //updateAttachmentsByCaseId(157519L); // PORTAL-670 Не поступают вложения от Тюмени в portal (интеграция)
        //updateNullIssueCreator();
        updateCaseObjectById(168646);
        //createCaseObjectByIssueId(210479);
    }

    private void updateCreationDateAttachments() {
        /* for one-time only before Redmine integration enabled */
        if (portalConfig.data().integrationConfig().isRedminePatchEnabled()) {
            logger.debug("Update creation date of issue attachments started");
            redmineEndpointDAO.getAll().forEach(redmineService::updateCreationDateAttachments);
            logger.debug("Update creation date of issue attachments ended");
        }
    }

    private void updateAttachmentsByCaseId(long caseId) {
        /* for one-time only before Redmine integration enabled */
        if (portalConfig.data().integrationConfig().isRedminePatchEnabled()) {
            logger.debug("Merge attachments of case object with id {} started", caseId);
            redmineService.updateAttachmentsByCaseId(caseId);
            logger.debug("Merge attachments of case object with id {} ended", caseId);
        }
    }

    private void updateCaseObjectById(long caseId) {
        /* for one-time only before Redmine integration enabled */
        if (portalConfig.data().integrationConfig().isRedminePatchEnabled()) {
            logger.debug("Update case object with id {} started", caseId);
            redmineService.updateCaseObjectById(caseId);
            logger.debug("Update case object with id {} ended", caseId);
        }
    }

    private void createCaseObjectByIssueId(int issueId) {
        /* for one-time only before Redmine integration enabled */
        if (portalConfig.data().integrationConfig().isRedminePatchEnabled()) {
            logger.debug("Create case object by issue with id {} started", issueId);
            redmineService.createCaseObjectByIssue(issueId, 2934, "296");
            logger.debug("Create case object by issue with id {} ended", issueId);
        }
    }

    private void updateNullIssueCreator() {
        if (portalConfig.data().integrationConfig().isRedmineEnabled()) {
            logger.debug("Update null issue creator started");
            boolean result = caseObjectDAO.updateNullCreatorByExtAppType(En_ExtAppType.REDMINE.getCode());
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
