package ru.protei.portal.jira.handlers;

import com.atlassian.jira.issue.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.jira.service.JiraService;

public class JiraBackchannelHandlerImpl implements JiraBackchannelHandler {
    @Override
    public void handle(AssembledCaseEvent event) {
        logger.debug("Handling action on jira-related issue in Portal-CRM");
        if (!portalConfig.data().integrationConfig().isJiraEnabled()) {
            logger.debug("Jira integration is disabled, nothing happens");
            return;
        }

        final long caseId = event.getCaseObject().getId();

        logger.debug("Modified object has id: {}", caseId);

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

        final JiraEndpoint endpoint = endpointDAO.getByProjectId(Long.parseLong(projectId));
        if (endpoint == null) {
            logger.debug("Endpoint was not found for projectId {}", projectId);
            return;
        }

        logger.debug("Using endpoint for server: {}", endpoint.getServerAddress());

        final Issue issue = service.getIssueById(issueId, endpoint);
        if (issue == null) {
            logger.debug("Issue with id {} was not found", issueId);
            return;
        }

        logger.debug("Updating comments");
        updateComments(issue, event.getCaseComment(), endpoint);
        logger.debug("Finished updating of comments");

        logger.debug("Copying case object changes to redmine issue");
        updateIssueProps(issue, event, endpoint);

        service.updateIssue(issue, endpoint);
    }

    private void updateIssueProps(Issue issue, AssembledCaseEvent event, JiraEndpoint endpoint) {
        final long priorityMapId = endpoint.getPriorityMapId();
        final long statusMapId = endpoint.getStatusMapId();

        final CaseObject oldObj = event.getInitState();
        final CaseObject newObj = event.getLastState();

        logger.debug("Trying to get redmine priority level id matching with portal: {}", newObj.getImpLevel());
        final JiraPriorityMapEntry jiraPriorityMapEntry =
                priorityMapEntryDAO.getByPortalPriorityId(newObj.getImpLevel(), priorityMapId);
        if (jiraPriorityMapEntry != null) {
            logger.debug("Found redmine priority level name: {}", jiraPriorityMapEntry.getRedminePriorityName());
            issue.getCustomFieldById(RedmineUtils.REDMINE_CUSTOM_FIELD_ID)
                    .setValue(jiraPriorityMapEntry.getRedminePriorityName());
        } else
            logger.debug("Redmine priority level not found");

        logger.debug("Trying to get redmine status id matching with portal: {} -> {}", oldObj.getStateId(), newObj.getStateId());
        final JiraStatusMapEntry jiraStatusMapEntry =
                statusMapEntryDAO.getRedmineStatus(oldObj.getState(), newObj.getState(), statusMapId);
        if (jiraStatusMapEntry != null && newObj.getState() != En_CaseState.VERIFIED) {
            logger.debug("Found redmine status id: {}", jiraStatusMapEntry.getRedmineStatusId());
            issue.setStatusId(jiraStatusMapEntry.getRedmineStatusId());
        } else
            logger.debug("Redmine status not found");

        issue.setDescription(newObj.getInfo());
        issue.setSubject(newObj.getName());
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
