package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.redmine.service.RedmineService;
import ru.protei.portal.redmine.utils.LoggerUtils;
import ru.protei.portal.redmine.utils.RedmineUtils;

import java.util.List;

public final class RedmineBackChannelHandler implements BackchannelEventHandler {

    @Override
    public void handle(AssembledCaseEvent event) {
        logger.debug("Handling action on redmine-related issue in Portal-CRM");
        if (!(portalConfig.data().integrationConfig().isRedmineEnabled() || portalConfig.data().integrationConfig().isRedmineBackchannelEnabled())) {
            logger.debug("Redmine integration disabled in config, nothing happens");
            return;
        }

        final long caseId = event.getCaseObject().getId();

        logger.debug("Modified object has id: {}", caseId);

        /** PORTAL-162
         * Я не понял, зачем нужно в back-channel делать сохранение case?
         * Задача back-channel - отправлять сигнал об изменениях удаленной стороне,
         * а не вносить изменения локально. Если для работы redmine-plugin'a требуется
         * обновление какого-либо отдельного поля, то нужно сделать соответствующий
         * метод в DAO типа updateLastAccessTime (), в котором будет вызов partial-update,
         * для внесения изменений только в те поля, которые требуется.
         * А вообще такого быть не должно, здесь должна быть ТОЛЬКО отправка
         * сообщения удаленной стороне
         **/
//        final CaseObject caseObject = event.getCaseObject();
//        caseObjectDAO.saveOrUpdate(caseObject);

        String extAppId = externalCaseAppDAO.get(caseId).getExtAppCaseId();
        if (extAppId == null) {
            logger.debug("case {} has no ext-app-id", caseId);
            return;
        }

        final String[] issueAndCompanyIds = extAppId.split("_");

        if (issueAndCompanyIds.length != 2
                || !issueAndCompanyIds[0].matches("^[0-9]+$")
                || !issueAndCompanyIds[1].matches("^[0-9]+$")) {

            logger.debug("case {} has invalid ext-app-id : {}", caseId, extAppId);
            return;
        }

        final int issueId = Integer.parseInt(issueAndCompanyIds[0]);
        final String projectId = externalCaseAppDAO.get(caseId).getExtAppData();
        final long companyId = Long.parseLong(issueAndCompanyIds[1]);

        final RedmineEndpoint endpoint = endpointDAO.getByCompanyIdAndProjectId(companyId,
                projectId);
        if (endpoint == null) {
            logger.debug("Endpoint was not found for companyId {} and projectId {}", companyId, projectId);
            return;
        }

        logger.debug("Using endpoint for server: {}", endpoint.getServerAddress());

        final Issue issue = service.getIssueById(issueId, endpoint);
        if (issue == null) {
            logger.debug("Issue with id {} was not found", issueId);
            return;
        }

        logger.debug("Updating comments");
        if (event.isCommentAttached()) {
            updateComments(issue, event.getInitiator(), event.getAddedCaseComments());
        }
        logger.debug("Finished updating of comments");

        logger.debug("Copying case object changes to redmine issue");
        updateIssueProps(issue, event, endpoint);

        try {
            service.updateIssue(issue, endpoint);
        } catch (RedmineException e) {
            logger.error("Failed to update issue with id {}", issue.getId());
            LoggerUtils.logRedmineException(logger, e);
        }
    }

    private void updateIssueProps(Issue issue, AssembledCaseEvent event, RedmineEndpoint endpoint) {
        if (event.isCaseImportanceChanged()) {
            final long priorityMapId = endpoint.getPriorityMapId();
            logger.debug("Trying to get redmine priority level id matching with portal: {}", event.getLastCaseMeta().getImpLevel());
            final RedminePriorityMapEntry redminePriorityMapEntry = priorityMapEntryDAO.getByPortalPriorityId(event.getLastCaseMeta().getImpLevel(), priorityMapId);
            if (redminePriorityMapEntry != null) {
                logger.debug("Found redmine priority level name: {}", redminePriorityMapEntry.getRedminePriorityId());
                issue.setPriorityId(redminePriorityMapEntry.getRedminePriorityId());
            } else {
                logger.debug("Redmine priority level not found");
            }
        }

        if (event.isCaseStateChanged()) {
            final long statusMapId = endpoint.getStatusMapId();
            logger.debug("Trying to get redmine status id matching with portal: {} -> {}", event.getInitCaseMeta().getStateId(), event.getLastCaseMeta().getStateId());
            RedmineStatusMapEntry redmineStatusMapEntry = statusMapEntryDAO.getRedmineStatus(event.getInitCaseMeta().getState(), event.getLastCaseMeta().getState(), statusMapId);
            if (redmineStatusMapEntry != null && event.getLastCaseMeta().getState() != En_CaseState.VERIFIED) {
                logger.debug("Found redmine status id: {}", redmineStatusMapEntry.getRedmineStatusId());
                issue.setStatusId(redmineStatusMapEntry.getRedmineStatusId());
            } else {
                logger.debug("Redmine status not found");
            }
        }

        if (event.getName().hasDifferences()) {
            issue.setSubject(event.getName().getNewState());
        }
        if (event.getInfo().hasDifferences()) {
            issue.setDescription(event.getName().getNewState());
        }
    }

    private void updateComments(Issue issue, Person initiator, List<CaseComment> addedCaseComments) {
        CaseComment comment = CollectionUtils.last(addedCaseComments);
        if (!comment.getText().isEmpty() && !comment.isPrivateComment()) {
            issue.setNotes(RedmineUtils.COMMENT_PROTEI_USER_PREFIX + ": " + initiator.getDisplayName() + ": " + comment.getText());
        }
    }

    @Autowired
    private RedmineEndpointDAO endpointDAO;

    @Autowired
    private RedmineService service;


    @Autowired
    private RedminePriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private RedmineStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    private PortalConfig portalConfig;

    private static final Logger logger = LoggerFactory.getLogger(RedmineBackChannelHandler.class);
}
