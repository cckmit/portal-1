package ru.protei.portal.jira.factory;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.PersonService;
import ru.protei.portal.jira.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;

public class JiraEventTypeHandlersFactory {
    private static final Logger logger = LoggerFactory.getLogger(JiraEventTypeHandlersFactory.class);
    private static final Map<Long, JiraEventTypeHandler> handlers =
            new HashMap<Long, JiraEventTypeHandler>() {{
                put(EventType.ISSUE_CREATED_ID, new JiraIssueCreatedEventHandler());
                put(EventType.ISSUE_UPDATED_ID, new JiraIssueUpdatedEventHandler());
            }};

    public static JiraEventTypeHandler returnHandler(long eventTypeId) {
        return handlers.getOrDefault(eventTypeId, x -> {logger.debug("No handler found for event type id {}", x); return null;});
    }

    public static class JiraIssueCreatedEventHandler implements JiraEventTypeHandler {
        @Autowired
        CaseService caseService;

        @Autowired
        PersonDAO personDAO;

        @Autowired
        JiraEndpointDAO jiraEndpointDAO;

        @Autowired
        private ExternalCaseAppDAO externalCaseAppDAO;

        @Autowired
        private CaseObjectDAO caseObjectDAO;

        @Override
        public CaseObject handle(IssueEvent event) {
            final Issue newJiraIssue = event.getIssue();
            final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(event.getProject().getId());
            final Person person = personDAO.get(jiraEndpointDAO.getCompanyUser(event.getProject().getId()));
            final CaseObject newCaseObject = CommonUtils.convertJiraIssueToPortalIssue(newJiraIssue, person, endpoint);
            caseObjectDAO.insertCase(newCaseObject);
            return newCaseObject;
        }
    }

    public static class JiraIssueUpdatedEventHandler implements JiraEventTypeHandler {
        @Autowired
        CaseService caseService;

        @Autowired
        PersonDAO personDAO;

        @Autowired
        JiraEndpointDAO jiraEndpointDAO;

        @Autowired
        private ExternalCaseAppDAO externalCaseAppDAO;

        @Autowired
        private CaseObjectDAO caseObjectDAO;

        @Override
        public CaseObject handle(IssueEvent event) {
            final Issue newJiraIssue = event.getIssue();
            final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(event.getProject().getId());
            final Person person = personDAO.get(jiraEndpointDAO.getCompanyUser(event.getProject().getId()));
            final String projectId = String.valueOf(event.getProject().getId());
            CaseObject caseObj = caseObjectDAO.getByExternalAppCaseId(projectId + "_" + newJiraIssue.getId());
            if (caseObj != null)
                caseObj = CommonUtils.updatePortalIssue(newJiraIssue, caseObj, endpoint);
            else
                caseObj = CommonUtils.convertJiraIssueToPortalIssue(newJiraIssue, person, endpoint);
            caseObjectDAO.saveOrUpdate(caseObj);
            return caseObj;
        }
    }
}
