package ru.protei.portal.jira.factory;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.jira.utils.CommonUtils;

public class JiraIssueCreatedEventHandler implements JiraEventTypeHandler {
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
        final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(newJiraIssue.getProjectId());
        final Person person = personDAO.get(endpoint.getPersonId());
        final CaseObject newCaseObject = CommonUtils.convertJiraIssueToPortalIssue(newJiraIssue, person, endpoint);
        caseObjectDAO.insertCase(newCaseObject);
        final ExternalCaseAppData appData = new ExternalCaseAppData(newCaseObject);
        appData.setExtAppCaseId(newJiraIssue.getId() + "_" + newJiraIssue.getProjectId());
        appData.setExtAppData(String.valueOf(newJiraIssue.getProjectId()));
        externalCaseAppDAO.merge(appData);
        return newCaseObject;
    }
}