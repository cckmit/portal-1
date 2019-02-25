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

public class JiraIssueUpdatedEventHandler implements JiraEventTypeHandler {
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
        final String projectId = String.valueOf(newJiraIssue.getProjectId());
        CaseObject caseObj = caseObjectDAO.getByExternalAppCaseId(projectId + "_" + newJiraIssue.getId());
        if (caseObj != null)
            caseObj = CommonUtils.updatePortalIssue(newJiraIssue, caseObj, endpoint);
        else {
            caseObj = CommonUtils.convertJiraIssueToPortalIssue(newJiraIssue, person, endpoint);
            final ExternalCaseAppData appData = new ExternalCaseAppData(caseObj);
            appData.setExtAppCaseId(newJiraIssue.getId() + "_" + newJiraIssue.getProjectId());
            appData.setExtAppData(String.valueOf(newJiraIssue.getProjectId()));
            externalCaseAppDAO.merge(appData);
        }
        caseObjectDAO.saveOrUpdate(caseObj);
        return caseObj;
    }
}
