package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.AttachmentInput;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.utils.JiraUtils;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.utils.CommonUtils;
import ru.protei.portal.jira.utils.CustomJiraIssueParser;

import java.util.*;

public class JiraBackchannelHandlerImpl implements JiraBackchannelHandler {

    @Autowired
    JiraClientFactory clientFactory;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    FileStorage fileStorage;

    @Override
    public void handle(AssembledCaseEvent event) {
        logger.debug("Handling action on jira-related issue in Portal-CRM");
        if (!portalConfig.data().integrationConfig().isJiraEnabled()) {
            logger.debug("Jira integration is disabled, no actions taken");
            return;
        }

        if (!event.isCoreModuleEvent()) {
            logger.debug("skip handle plugin-published event for {}", event.getCaseObject().getExtId());
            return;
        }

        final CaseObject object = event.getCaseObject();

        if (object.isDeleted()) {
            logger.debug("case object {} is deleted, skip", object.defGUID());
            return;
        }

        if (object.isPrivateCase()) {
            logger.debug("case object {} is private, skip", object.defGUID());
            return;
        }

//        final long caseId = object.getId();

        logger.debug("Modified object has id: {}", object.getId());

        ExternalCaseAppData extCaseData = externalCaseAppDAO.get(object.getId());
        if (extCaseData.getExtAppCaseId() == null) {
            logger.debug("case {} has no ext-case-id, skip", object.getId());
            return;
        }

        // TODO why no check if its jira issue (by ExternalCaseAppData.extAppType)

        final JiraUtils.JiraIssueData issueData = JiraUtils.convert(extCaseData);

        final JiraEndpoint endpoint = endpointDAO.get(issueData.endpointId);
        if (endpoint == null) {
            logger.debug("Endpoint was not found for projectId {}, case = {}", issueData.projectId, object.getId());
            return;
        }

        clientFactory.run(endpoint, client -> {
            logger.debug("Using endpoint for server: {}", endpoint.getServerAddress());
            final Issue issue = client.getIssueClient().getIssue(issueData.key).claim();
            if (issue == null) {
                logger.debug("Issue with key {} was not found", issueData.key);
                return;
            }

            IssueRestClient issueClient = client.getIssueClient();

            if (isRequireGenericDataUpdate(event)){
                generalUpdate(endpoint, event, issue, issueClient);
            }

            if (event.isCommentAttached()) {
                event.getAddedCaseComments().forEach(comment -> {
                    if (!comment.isPrivateComment()) {
                        logger.debug("add comment {} to issue {}", comment.getId(), issue.getKey());
                        issueClient.addComment(issue.getCommentsUri(), convertComment(comment, event.getInitiator())).claim();
                    }});
            }

            if (event.getAddedAttachments() != null) {
                issueClient.addAttachments(issue.getAttachmentsUri(), buildAttachmentsArray(event.getAddedAttachments())).claim();
            }
        });
    }

    private Comment convertComment (CaseComment ourComment, Person initiator) {
        return Comment.valueOf(TransliterationUtils.transliterate(initiator.getLastName() + " " + initiator.getFirstName()) + "\r\n" + ourComment.getText());
    }

    private AttachmentInput[] buildAttachmentsArray (Collection<Attachment> ourAttachments) {
        List<AttachmentInput> result = new ArrayList<>(ourAttachments.size());

        for (Attachment a : ourAttachments) {
            FileStorage.File file =  fileStorage.getFile(a.getExtLink());
            if (file == null) {
                logger.debug("unable to get file from storage for link : {}", a.getExtLink());
                continue;
            }
            result.add(new AttachmentInput(a.getFileName(), file.getData()));
        }

        return result.toArray(new AttachmentInput[]{});
    }

    private void generalUpdate(JiraEndpoint endpoint, AssembledCaseEvent event, Issue issue, IssueRestClient issueClient) {
//        final IssueInputBuilder issueInputParameters = new IssueInputBuilder();
//
//        issueInputParameters
//                .setSummary(object.getName());
//                .setDescription(object.getInfo());

        final CaseObject object = event.getCaseObject();

        if (event.isCaseStateChanged()) {
            String newJiraStatus = statusMapEntryDAO.getJiraStatus(endpoint.getStatusMapId(), object.getState());
            logger.debug("send change state, new jira-state: {}", newJiraStatus);

            Map<String,Integer> stateTransitions = new HashMap<>();
            issueClient.getTransitions(issue).claim().forEach(t -> stateTransitions.put(t.getName(), t.getId()));

            if (stateTransitions.containsKey(newJiraStatus)) {
                int transitionId = stateTransitions.get(newJiraStatus);
                logger.debug("ok, we have jira transition for it, name={}, id={}, invoke", newJiraStatus, transitionId);

                issueClient.transition(issue, new TransitionInput(transitionId)).claim();
            } else {
                logger.warn("issue {} has no transition for status {}, available set is {}, skip changes", issue.getKey(), newJiraStatus, stateTransitions.keySet());
            }
        }

        if (event.isCaseImportanceChanged()) {
            logger.debug("case priority is changed, try find jira-value");
            JiraPriorityMapEntry priorityMapEntry = priorityMapEntryDAO.getByPortalPriorityId(endpoint.getPriorityMapId(), object.importanceLevel());

            if (priorityMapEntry != null) {
                logger.debug("ok, found jira-severity field value {} for our {}, send changes", priorityMapEntry.getJiraPriorityName(), object.importanceLevel());

                IssueInputBuilder builder = new IssueInputBuilder();
                builder.setFieldValue(CustomJiraIssueParser.CUSTOM_FIELD_SEVERITY, ComplexIssueInputFieldValue.with("value", priorityMapEntry.getJiraPriorityName()));
                issueClient.updateIssue(issue.getKey(), builder.build()).claim();
            }
            else {
                logger.debug("unable to find jira-severity value for our level {}", object.importanceLevel());
            }
        }


//        issueClient.updateIssue(issueData.key, issueInputParameters.build()).done(
//                aVoid ->
//                        logger.debug("ok, issue {} was handled, case {}", issueData.key, object.getId())
//        )
//        .fail(throwable ->
//                logger.debug("unable to send changes for case {}, issue={}", object.getId(), issueData.key, throwable)
//        ).claim();
    }

    private boolean isRequireGenericDataUpdate (AssembledCaseEvent event) {
        return event.isCaseStateChanged() || event.isCaseImportanceChanged();
    }


    @Autowired
    private JiraEndpointDAO endpointDAO;

    @Autowired
    private JiraPriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private JiraStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    private PortalConfig portalConfig;

    private static final Logger logger = LoggerFactory.getLogger(JiraBackchannelHandlerImpl.class);
}
