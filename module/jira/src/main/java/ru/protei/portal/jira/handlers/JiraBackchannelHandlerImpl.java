package ru.protei.portal.jira.handlers;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.AttachmentInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            logger.debug("Jira integration is disabled, nothing happens");
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

        final CommonUtils.IssueData issueData = CommonUtils.convert(extCaseData);

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
                generalUpdate(event, object, issueData, issueClient);
            }

            if (event.getCaseComment() != null) {
                logger.debug("add comment {} to issue {}", event.getCaseComment().getId(), issue.getKey());
                issueClient.addComment(issue.getCommentsUri(), convertComment(event.getCaseComment()))
                        .claim();
            }

            if (event.getAddedAttachments() != null) {
                issueClient.addAttachments(issue.getAttachmentsUri(), buildAttachmentsArray(event.getAddedAttachments())).claim();
            }
        });
    }

    private Comment convertComment (CaseComment ourComment) {
        return Comment.valueOf(ourComment.getAuthor().getDisplayShortName() + "\r\n" + ourComment.getText());
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

    private void generalUpdate(AssembledCaseEvent event, CaseObject object, CommonUtils.IssueData issueData, IssueRestClient issueClient) {
        final IssueInputBuilder issueInputParameters = new IssueInputBuilder();

        issueInputParameters
                .setSummary(object.getName())
                .setDescription(object.getInfo());

        if (event.isCaseStateChanged()) {
            String newJiraStatus = statusMapEntryDAO.getJiraStatus(object.getState());
            logger.debug("send change state, new jira-state: {}", newJiraStatus);
            issueInputParameters.setFieldValue(IssueFieldId.STATUS_FIELD.id, newJiraStatus);
        }

        issueClient.updateIssue(issueData.key, issueInputParameters.build()).done(
                aVoid ->
                        logger.debug("ok, issue {} was handled, case {}", issueData.key, object.getId())
        )
        .fail(throwable ->
                logger.debug("unable to send changes for case {}, issue={}", object.getId(), issueData.key, throwable)
        ).claim();
    }

    private boolean isRequireGenericDataUpdate (AssembledCaseEvent event) {
        return event.isInfoChanged() || event.isNameChanged() || event.isCaseStateChanged();
    }


    @Autowired
    private JiraEndpointDAO endpointDAO;

//    @Autowired
//    private CaseObjectDAO caseObjectDAO;

//    @Autowired
//    private JiraPriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private JiraStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    private PortalConfig portalConfig;

    private static final Logger logger = LoggerFactory.getLogger(JiraBackchannelHandlerImpl.class);
}
