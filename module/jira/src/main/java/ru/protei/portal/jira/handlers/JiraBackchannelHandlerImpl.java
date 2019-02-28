package ru.protei.portal.jira.handlers;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.jira.service.JiraService;

public class JiraBackchannelHandlerImpl implements JiraBackchannelHandler {
    private final IssueService issueService = ComponentAccessor.getIssueService();
    private final UserManager userService = ComponentAccessor.getUserManager();
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

        String extAppId = externalCaseAppDAO.get(caseId).getExtAppCaseId();
        if (extAppId == null) {
            logger.debug("case {} has no ext-app-id", caseId);
            return;
        }

        final String[] issueAndProjectIds = extAppId.split("_");

        if (issueAndProjectIds.length != 2) {
            logger.debug("case {} has invalid ext-app-id : {}", caseId, extAppId);
            return;
        }

        final long issueId = Long.parseLong(issueAndProjectIds[0]);
        final String projectId = issueAndProjectIds[1];

        final JiraEndpoint endpoint = endpointDAO.getByProjectId(Long.parseLong(projectId));
        if (endpoint == null) {
            logger.debug("Endpoint was not found for projectId {}", projectId);
            return;
        }

        logger.debug("Using endpoint for server: {}", endpoint.getServerAddress());
        final ApplicationUser user = userService.getUserByName("protei_tech_user");
        final Issue issue = issueService.getIssue(user, issueId).getIssue();
        if (issue == null) {
            logger.debug("Issue with id {} was not found", issueId);
            return;
        }

        final IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
        issueInputParameters
                .setSummary(object.getName())
                .setDescription(object.getInfo())
                .setStatusId(statusMapEntryDAO.getJiraStatus(object.getStateId()));

        IssueService.UpdateValidationResult updateValidationResult = issueService
                .validateUpdate(user, issueId, issueInputParameters);

        if (updateValidationResult.isValid())
        {
            IssueService.IssueResult updateResult = issueService.update(user, updateValidationResult);
            if (!updateResult.isValid())
            {
                logger.debug("Something is wrong, Jira issue couldn't be updated");
            }
        }

//        updateComments(issue, event.getCaseComment(), endpoint);

        logger.debug("Copying case object changes to redmine issue");
    }

    private void updateComments(Issue issue, CaseComment comment, JiraEndpoint endpoint) {

    }

    @Autowired
    private JiraEndpointDAO endpointDAO;

    @Autowired
    private JiraService service;

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
