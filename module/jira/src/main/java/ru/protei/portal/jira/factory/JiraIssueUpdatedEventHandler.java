package ru.protei.portal.jira.factory;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.*;
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

    @Autowired
    private JiraStatusMapEntryDAO jiraStatusMapEntryDAO;

    @Autowired
    private JiraPriorityMapEntryDAO jiraPriorityMapEntryDAO;

    @Override
    public CaseObject handle(IssueEvent event) {
        final Issue issue = event.getIssue();
        final JiraEndpoint endpoint = jiraEndpointDAO.getByProjectId(issue.getProjectId());
        final Person person = personDAO.get(endpoint.getPersonId());
        final String projectId = String.valueOf(issue.getProjectId());
        CaseObject caseObj = caseObjectDAO.getByExternalAppCaseId(issue.getId() + "_" + projectId);
        if (caseObj != null) {
            caseObj = CommonUtils.updatePortalIssue(issue, caseObj, endpoint);
            caseObjectDAO.saveOrUpdate(caseObj);
        }
        else {
            caseObj = new CaseObject();
            caseObj.setCaseType(En_CaseType.CRM_SUPPORT);
            caseObj.setCreated(issue.getCreated());
            caseObj.setModified(issue.getUpdated());
            caseObj.setInitiator(person);
            caseObj.setExtAppType("jira_nexign");
            caseObj.setStateId(jiraStatusMapEntryDAO.getByJiraStatus(issue.getStatusId()));
            final JiraPriorityMapEntry entry = jiraPriorityMapEntryDAO.getByJiraPriorityId(issue.getIssueTypeId());
            if (entry != null)
                caseObj.setImpLevel(entry.getLocalPriorityId());
            else
                caseObj.setImpLevel(3);
            caseObj.setName(issue.getSummary());
            caseObj.setInfo(issue.getDescription() + "\r\n" + issue.getIssueType());
            caseObj.setLocal(0);
            caseObj.setInitiatorCompanyId(endpoint.getCompanyId());

            caseObjectDAO.insertCase(caseObj);

            final ExternalCaseAppData appData = new ExternalCaseAppData(caseObj);
            appData.setExtAppCaseId(issue.getId() + "_" + issue.getProjectId());
            appData.setExtAppData(String.valueOf(issue.getProjectId()));
            externalCaseAppDAO.merge(appData);
        }
        return caseObj;
    }
}
