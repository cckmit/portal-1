package ru.protei.portal.jira.handlers;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.utils.CommonUtils;

public class JiraBackchannelHandlerImpl implements JiraBackchannelHandler {
    @Autowired
    JiraClientFactory clientFactory;

    @Autowired
    PersonDAO personDAO;

    @Override
    public void handle(AssembledCaseEvent event) {
        logger.debug("Handling action on jira-related issue in Portal-CRM");
        if (!portalConfig.data().integrationConfig().isJiraEnabled()) {
            logger.debug("Jira integration is disabled, nothing happens");
            return;
        }

        final CaseObject object = event.getCaseObject();

        final long caseId = event.getCaseObject().getId();

        logger.debug("Modified object has id: {}", caseId);

        ExternalCaseAppData extCaseData = externalCaseAppDAO.get(caseId);
        if (extCaseData == null || extCaseData.getExtAppCaseId() == null) {
            logger.debug("case {} has no ext-app-id", caseId);
            return;
        }

        final CommonUtils.IssueData issueData = CommonUtils.convert(extCaseData);


        final JiraEndpoint endpoint = endpointDAO.getByProjectId(issueData.endpointId);
        if (endpoint == null) {
            logger.debug("Endpoint was not found for projectId {}, case = {}", issueData.projectId, caseId);
            return;
        }

        clientFactory.run(endpoint, client -> {
            logger.debug("Using endpoint for server: {}", endpoint.getServerAddress());
            final Issue issue = client.getIssueClient().getIssue(issueData.key).claim();
            if (issue == null) {
                logger.debug("Issue with key {} was not found", issueData.key);
                return;
            }

            final IssueInputBuilder issueInputParameters = new IssueInputBuilder();

            issueInputParameters
                    .setSummary(object.getName())
                    .setDescription(object.getInfo())
                    .setFieldValue (IssueFieldId.STATUS_FIELD.id, statusMapEntryDAO.getJiraStatus(object.getState()));

            client.getIssueClient().updateIssue(issueData.key, issueInputParameters.build()).done(
                    aVoid -> logger.debug("ok, issue {} was handled, case {}", issueData.key, caseId)
                    //logger.debug("Copying case object changes to jira issue");
                    //updateComments(issue, event.getCaseComment(), endpoint);
            )
            .fail(throwable ->
                logger.debug("unable to send changes for case {}, issue={}", caseId, issueData.key, throwable)
            )
            ;
        });
    }

    private void updateComments(Issue issue, CaseComment comment, JiraEndpoint endpoint) {

    }

    @Autowired
    private JiraEndpointDAO endpointDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

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
