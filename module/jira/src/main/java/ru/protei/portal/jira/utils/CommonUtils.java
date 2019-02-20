package ru.protei.portal.jira.utils;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;

import java.util.HashMap;
import java.util.Map;

public class CommonUtils {
    private final static Logger logger = LoggerFactory.getLogger(CommonUtils.class);
    private static final Map<String, En_CaseType> mappingOfTypes = new HashMap<String, En_CaseType>(){{
        put("Improvement", En_CaseType.FREQ);
        put("Error", En_CaseType.BUG);
        put("Service", En_CaseType.TASK);
        put("Consultation", En_CaseType.OFFICIAL);
    }};

    private static final Map<String, En_CaseState> mappingOfStatuses = new HashMap<String, En_CaseState>(){{
        put("Authorized", En_CaseState.CREATED);
        put("Studying", En_CaseState.OPENED);
        put("Request to customer", En_CaseState.INFO_REQUEST);
        put("Postpone", En_CaseState.PAUSED);
        put("Soft close", En_CaseState.DONE);
        put("Nothing to change", En_CaseState.VERIFIED);
    }};


    public static CaseObject convertJiraIssueToPortalIssue(Issue issue, Person contactPerson, JiraEndpoint endpoint) {
        final CaseObject obj = new CaseObject();
        obj.setCreated(issue.getCreated());
        obj.setModified(issue.getUpdated());
        obj.setInitiator(contactPerson);
        obj.setCaseType(mappingOfTypes.getOrDefault(issue.getIssueType().getName(), En_CaseType.CRM_SUPPORT));
        obj.setExtAppType("jira_nexign");
        obj.setState(mappingOfStatuses.getOrDefault(issue.getStatus().getName(), En_CaseState.CREATED));
        obj.setName(issue.getSummary());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setInitiatorCompanyId(endpoint.getCompanyId());
        return obj;
    }

    public static Issue convertPortalIssueToJiraIssue(CaseObject caseObject) {
        final String extAppType = caseObject.getExtAppType();
        final String[] splitted = extAppType.split("_");
        if (splitted.length != 2)
            return null;

        final long issueId = Long.parseLong(splitted[1]);

        final ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("protei_tech_user");
        IssueService issueService = ComponentAccessor.getIssueService();
        final Issue issue = issueService.getIssue(user, issueId).getIssue();
        //retrieved issue

        return issue;
    }

    public static CaseObject updatePortalIssue(Issue issue, CaseObject obj, JiraEndpoint endpoint) {
        obj.setModified(issue.getUpdated());
        obj.setCaseType(mappingOfTypes.getOrDefault(issue.getIssueType().getName(), En_CaseType.CRM_SUPPORT));
        obj.setExtAppType("jira_nexign");
        issue.getPriority().getName();
        issue.getStatus().getName();
        issue.getExternalFieldValue("Urgency");
        obj.setStateId(0);
        obj.setState(En_CaseState.ACTIVE);
        obj.setName(issue.getSummary());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setInitiatorCompanyId(endpoint.getCompanyId());
        return null;
    }
}
