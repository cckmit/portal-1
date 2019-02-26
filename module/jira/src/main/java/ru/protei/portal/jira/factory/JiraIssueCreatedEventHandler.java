package ru.protei.portal.jira.factory;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
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

    @Autowired
    JiraStatusMapEntryDAO jiraStatusMapEntryDAO;

    @Autowired
    JiraPriorityMapEntryDAO jiraPriorityMapEntryDAO;

    @Override
    public CaseObject handle(IssueEvent event) {
        final Issue issue = event.getIssue();
        final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(issue.getProjectId());
        final Person person = personDAO.get(endpoint.getPersonId());
        final CaseObject caseObj = new CaseObject();
        caseObj.setCreated(issue.getCreated());
        caseObj.setModified(issue.getUpdated());
        caseObj.setInitiator(person);
        caseObj.setExtAppType("jira_nexign");
        caseObj.setStateId(jiraStatusMapEntryDAO.getByJiraStatus(issue.getStatusId()));
        caseObj.setImpLevel(jiraPriorityMapEntryDAO.getByJiraPriorityId(issue.getIssueTypeId()).getLocalPriorityId());
        caseObj.setName(issue.getSummary());
        caseObj.setInfo(issue.getDescription() + "\r\n" + issue.getIssueType());
        caseObj.setLocal(0);
        caseObj.setInitiatorCompanyId(endpoint.getCompanyId());
        final ExternalCaseAppData appData = new ExternalCaseAppData(caseObj);
        appData.setExtAppCaseId(issue.getId() + "_" + issue.getProjectId());
        appData.setExtAppData(String.valueOf(issue.getProjectId()));
        externalCaseAppDAO.merge(appData);
        caseObjectDAO.insertCase(caseObj);
        return caseObj;
    }
}