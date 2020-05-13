package ru.protei.portal.redmine.handlers;

import com.taskadapter.redmineapi.bean.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.redmine.service.CommonService;
import ru.protei.portal.redmine.service.RedmineService;

import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public final class RedmineBackChannelHandler implements BackchannelEventHandler {

    @EventListener
    public void onAssembledCaseEvent(AssembledCaseEvent event) {
        if (!event.isCoreModuleEvent()) {
            logger.info("Skip handle core module event for id={} extId={}", event.getCaseObjectId(), event.getExtId());
            return;
        }
        if (!(portalConfig.data().integrationConfig().isRedmineEnabled() || portalConfig.data().integrationConfig().isRedmineBackchannelEnabled())) {
            logger.info("Redmine integration disabled in config, nothing happens");
            return;
        }
        logger.info("Handling action on redmine-related issue in Portal-CRM");
        try {

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

            if (commonService.getExternalAppId(caseId).isError()) {
                logger.debug("case {} has no ext-app-id", caseId);
                return;
            }

            commonService.getExternalCaseAppData(caseId)
                    .flatMap(this::findEndpointAndIssueId)
                    .flatMap(endpointAndIssueId -> proceedUpdate(endpointAndIssueId.IssueId, event, endpointAndIssueId.endpoint))
                    .ifError(result -> logger.warn(result.getMessage()));


            logger.info("case-object event handled for case {}", event.getCaseObject().getExtId());
        } catch (Exception e) {
            logger.error("error while handling event for case " + event.getCaseObject().getExtId(), e);
        }
    }

    private Result<EndpointAndIssueId> findEndpointAndIssueId(ExternalCaseAppData externalCaseAppData) {
        String projectId = externalCaseAppData.getExtAppData();
        return parseExtAppId(externalCaseAppData.getExtAppCaseId())
                .flatMap(issueAndCompanyIds -> {
                    int issueId = Integer.parseInt(issueAndCompanyIds[0]);
                    long companyId = Long.parseLong(issueAndCompanyIds[1]);
                    return commonService.getEndpoint( companyId, projectId )
                            .map(redmineEndpoint -> new EndpointAndIssueId(redmineEndpoint, issueId));
                });
    }

    private Result<Issue> proceedUpdate(Integer issueId, AssembledCaseEvent event, RedmineEndpoint endpoint) {
        return  service.getIssueById(issueId, endpoint)
                .map(issue -> updateComments(issue, event))
                .flatMap(issue -> uploadAttachment(issue, event, endpoint))
                .map(issue -> updateIssueProps(issue, event, endpoint))
                .flatMap(issue -> service.updateIssue(issue, endpoint));
    }

    private Result<String[]> parseExtAppId(String extAppId) {
        final String[] issueAndCompanyIds = extAppId.split("_");

        if (issueAndCompanyIds.length != 2
                || !issueAndCompanyIds[0].matches("^[0-9]+$")
                || !issueAndCompanyIds[1].matches("^[0-9]+$")) {

            return error(En_ResultStatus.INTERNAL_ERROR, String.format("case has invalid ext-app-id : %s", extAppId));
        }
        return ok(issueAndCompanyIds);
    }

    private Issue updateComments(Issue issue, AssembledCaseEvent event) {
        logger.debug("Updating comments");
        if (event.isCommentAttached()) {
            updateComments(issue, event.getInitiator(), event.getAddedCaseComments());
        }
        logger.debug("Finished updating of comments");
        return issue;
    }

    private Result<Issue> uploadAttachment(Issue issue, AssembledCaseEvent event, RedmineEndpoint endpoint) {
        if (event.getAddedAttachments() == null) {
            logger.debug("No attachments for Updating.");
            return ok(issue);
        }
        logger.debug( "Updating attachment" );
        return service.uploadAttachment( event.getAddedAttachments(), endpoint ).map( attachments -> {
            attachments.forEach( issue::addAttachment );
            logger.debug( "Finished updating of attachment" );
            return issue;
        } );
    }

    private Issue updateIssueProps(Issue issue, AssembledCaseEvent event, RedmineEndpoint endpoint) {
        if (event.isCaseImportanceChanged()) {
            final long priorityMapId = endpoint.getPriorityMapId();
            logger.debug("Trying to get redmine priority level id matching with portal: {}", event.getLastCaseMeta().getImpLevel());
            final RedminePriorityMapEntry redminePriorityMapEntry = commonService.getByPortalPriorityId(event.getLastCaseMeta().getImpLevel(), priorityMapId).getData();
            if (redminePriorityMapEntry != null) {
                logger.debug("Found redmine priority level name: {}", redminePriorityMapEntry.getRedminePriorityId());
                issue.setPriorityId(redminePriorityMapEntry.getRedminePriorityId());
            } else {
                logger.debug("Redmine priority level not found");
            }
        }

        if (event.isCaseStateChanged()) {
            final long statusMapId = endpoint.getStatusMapId();
            logger.debug("Trying to get redmine status id matching with portal: {} -> {}",
                    event.getInitCaseMeta().getStateId(), event.getLastCaseMeta().getStateId());
            RedmineStatusMapEntry redmineStatusMapEntry = commonService.getRedmineStatus(
                    event.getInitCaseMeta().getStateId(), event.getLastCaseMeta().getStateId(), statusMapId).getData();
            if (redmineStatusMapEntry != null
                    && En_CaseState.VERIFIED.getId() != event.getLastCaseMeta().getStateId()) {
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

        return issue;
    }

    private void updateComments(Issue issue, Person initiator, List<CaseComment> addedCaseComments) {
        CaseComment comment = CollectionUtils.last(addedCaseComments);
        if (!comment.getText().isEmpty() && !comment.isPrivateComment()) {
            issue.setNotes( COMMENT_PROTEI_USER_PREFIX + ": " + initiator.getDisplayName() + ": " + comment.getText());
        }
    }

    private static final String COMMENT_PROTEI_USER_PREFIX = "PROTEI";

    @Autowired
    private RedmineService service;
    @Autowired
    private CommonService commonService;
    @Autowired
    private PortalConfig portalConfig;

    private static class EndpointAndIssueId {
        public RedmineEndpoint endpoint;
        public Integer IssueId;

        public EndpointAndIssueId(RedmineEndpoint endpoint, Integer issueId) {
            this.endpoint = endpoint;
            this.IssueId = issueId;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(RedmineBackChannelHandler.class);

}
