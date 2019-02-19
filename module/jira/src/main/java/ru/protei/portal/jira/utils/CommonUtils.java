package ru.protei.portal.jira.utils;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.renderer.IssueRenderContext;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommonUtils {
    private final static Logger logger = LoggerFactory.getLogger(CommonUtils.class);
    private static final Map<String, En_CaseType> mappingOfTypes = new HashMap<String, En_CaseType>(){{
        put("Improvement", En_CaseType.FREQ);
        put("Error", En_CaseType.BUG);
        put("Service", En_CaseType.TASK);
        put("Consultation", En_CaseType.OFFICIAL);
    }};

    private static final Map<String, Integer> mappingOfPriorities = new HashMap<String, Integer>() {{
        put("A-Urgent", 1);

    }};


    public static CaseObject convertJiraIssueToPortalIssue(Issue issue, Person contactPerson, JiraEndpoint endpoint) {
        final CaseObject obj = new CaseObject();
        obj.setCreated(issue.getCreated());
        obj.setModified(issue.getUpdated());
        obj.setInitiator(contactPerson);
        obj.setCaseType(mappingOfTypes.getOrDefault(issue.getIssueType().getName(), En_CaseType.CRM_SUPPORT));
        obj.setExtAppType("jira_nexign");
        issue.getPriority().getName();
        issue.getStatus().getName();
        issue.getExternalFieldValue("Urgency");
        obj.setImpLevel(mappingOfPriorities.getOrDefault(issue.getPriority().getName(), 0));
        obj.setStateId(0);
        obj.setState(En_CaseState.ACTIVE);
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
        issueService.getIssue(user, issueId);
        //retrieved issue


    }
}
