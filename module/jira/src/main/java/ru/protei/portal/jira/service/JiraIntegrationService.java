package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.jira.utils.CommonUtils;
import ru.protei.portal.jira.utils.JiraHookEventData;

public class JiraIntegrationService {

    private static Logger logger = LoggerFactory.getLogger(JiraIntegrationService.class);

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
        caseObj.setCaseType(En_CaseType.CRM_SUPPORT);
        caseObj.setCreated(issue.getCreationDate().toDate());
        caseObj.setModified(issue.getUpdateDate().toDate());
        caseObj.setInitiator(person);
        caseObj.setExtAppType("jira_nexign");

        updateCaseState(issue, caseObj);
        updateCasePriority(issue, caseObj);

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

            // @FIXME set this by condition, our date can be newer
            caseObj.setModified(issue.getUpdateDate().toDate());
            caseObj.setExtAppType("jira_nexign");

            updateCaseState(issue, caseObj);
            updateCasePriority(issue, caseObj);

            caseObj.setName(issue.getSummary());
            caseObj.setInfo(issue.getDescription());
            caseObj.setLocal(0);
            caseObj.setInitiatorCompanyId(endpoint.getCompanyId());

            caseObjectDAO.saveOrUpdate(caseObj);
        }
        else {
            caseObj = createCaseObject(issue, endpoint, person);
        }
        return caseObj;

    }

    private void updateCaseState(Issue issue, CaseObject caseObj) {
        En_CaseState state = jiraStatusMapEntryDAO.getByJiraStatus(issue.getStatus().getName());
        if (state == null)
            throw new RuntimeException("unable to map jira-status " + issue.getStatus().getName() + " to portal case-state");

        caseObj.setState(state);
    }

    private void updateCasePriority(Issue issue, CaseObject caseObj) {
        JiraPriorityMapEntry jiraPriorityEntry = jiraPriorityMapEntryDAO.getByJiraPriorityId(issue.getPriority().getName());
        if (jiraPriorityEntry == null) {
            logger.warn("unable to map jira-priority level : {}, set as basic", issue.getPriority().getName());
            caseObj.setImpLevel(En_ImportanceLevel.BASIC.getId());
        }
        else {
            caseObj.setImpLevel(jiraPriorityEntry.getLocalPriorityId());
        }
    }
}
