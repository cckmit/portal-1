package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.jira.utils.CommonUtils;
import ru.protei.portal.jira.utils.JiraHookEventData;

public class JiraIntegrationService {
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
    private JiraStatusMapEntryDAO jiraStatusMapEntryDAO;

    @Autowired
    private JiraPriorityMapEntryDAO jiraPriorityMapEntryDAO;


    public CaseObject create (JiraHookEventData event) {
        final Issue issue = event.getIssue();
        final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(issue.getProject().getId());
        final Person person = personDAO.get(endpoint.getPersonId());
        return createCaseObject(issue, endpoint, person);
    }

    private CaseObject createCaseObject(Issue issue, JiraEndpoint endpoint, Person person) {
        final CaseObject caseObj = new CaseObject();
        caseObj.setCreated(issue.getCreationDate().toDate());
        caseObj.setModified(issue.getUpdateDate().toDate());
        caseObj.setInitiator(person);
        caseObj.setExtAppType("jira_nexign");
        caseObj.setStateId(jiraStatusMapEntryDAO.getByJiraStatus(issue.getStatus().getDescription()));
        caseObj.setImpLevel(jiraPriorityMapEntryDAO.getByJiraPriorityId(issue.getPriority().getName()).getLocalPriorityId());
        caseObj.setName(issue.getSummary());
        caseObj.setInfo(issue.getDescription() + "\r\n" + issue.getIssueType());
        caseObj.setLocal(0);
        caseObj.setInitiatorCompanyId(endpoint.getCompanyId());
        caseObjectDAO.insertCase(caseObj);

        final ExternalCaseAppData appData = new ExternalCaseAppData(caseObj);
        appData.setExtAppCaseId(CommonUtils.makeExternalIssueID(endpoint, issue));
        appData.setExtAppData(CommonUtils.makeExtAppData(issue));
        appData.setId(caseObj.getId());

        externalCaseAppDAO.merge(appData);

        return caseObj;
    }

    public CaseObject updateOrCreate (JiraHookEventData event) {
        final Issue issue = event.getIssue();
        final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(issue.getProject().getId());
        final Person person = personDAO.get(endpoint.getPersonId());

        CaseObject caseObj = caseObjectDAO.getByExternalAppCaseId(CommonUtils.makeExternalIssueID(endpoint, issue));
        if (caseObj != null) {
            caseObj = CommonUtils.updatePortalIssue(issue, caseObj, endpoint);
            caseObjectDAO.saveOrUpdate(caseObj);
        }
        else {
            caseObj = createCaseObject(issue, endpoint, person);
        }
        return caseObj;

    }
}
