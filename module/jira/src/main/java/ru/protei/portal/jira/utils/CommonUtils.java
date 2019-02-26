package ru.protei.portal.jira.utils;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.jira.entities.ShortenedIssue;

import java.util.HashMap;
import java.util.Map;

public class CommonUtils {
    private final static Logger logger = LoggerFactory.getLogger(CommonUtils.class);
    private static final Map<String, En_CaseType> mappingOfTypes = new HashMap<String, En_CaseType>() {{
        put("Improvement", En_CaseType.FREQ);
        put("Error", En_CaseType.BUG);
        put("Service", En_CaseType.TASK);
        put("Consultation", En_CaseType.OFFICIAL);
    }};

    private static final Map<String, En_CaseState> mappingOfStatuses = new HashMap<String, En_CaseState>() {{
        put("Authorized", En_CaseState.CREATED);
        put("Studying", En_CaseState.OPENED);
        put("Request to customer", En_CaseState.INFO_REQUEST);
        put("Postpone", En_CaseState.PAUSED);
        put("Soft close", En_CaseState.DONE);
        put("Nothing to change", En_CaseState.VERIFIED);
    }};


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
        obj.setExtAppType("jira_nexign");
        issue.getPriority();
        issue.getStatus().getName();
        obj.setStateId(0);
        obj.setState(En_CaseState.ACTIVE);
        obj.setName(issue.getSummary());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setInitiatorCompanyId(endpoint.getCompanyId());
        return null;
    }

    private static Issue buildIssueFromJson(JsonObject jsonObject) {
        final Long id = jsonObject.get("id").getAsLong();
        final JsonObject fields = jsonObject.get("fields").getAsJsonObject();

        final String created = fields.get("created").getAsString();
        final String updated = fields.get("updated").getAsString();
        final String summary = fields.get("summary").getAsString();
        final String description = fields.get("description").getAsString();
        final String statusName = fields.get("status").getAsJsonObject().get("name").getAsString();
        final Long projId = fields.get("project").getAsJsonObject().get("id").getAsLong();
        final String severityId = fields.get("customfield_12405").getAsJsonObject().get("value").getAsString().split("-")[0].trim();
        return new ShortenedIssue(
                id,
                created,
                updated,
                summary,
                description,
                statusName,
                projId,
                severityId
        );
    }

    private static Map buildIssueParamsFromJson(JsonObject jsonObject) {
        return null;
    }

    private static ApplicationUser buildUserFromJson(JsonObject jsonObject) {
        return new ApplicationUser() {
            @Override
            public String getKey() {
                return jsonObject.get("key").getAsString();
            }

            @Override
            public String getUsername() {
                return jsonObject.get("name").getAsString();
            }

            @Override
            public String getName() {
                return jsonObject.get("displayName").getAsString();
            }

            @Override
            public long getDirectoryId() {
                return 0;
            }

            @Override
            public boolean isActive() {
                return jsonObject.get("active").getAsBoolean();
            }

            @Override
            public String getEmailAddress() {
                return jsonObject.get("emailAddress").getAsString();
            }

            @Override
            public String getDisplayName() {
                return jsonObject.get("displayName").getAsString();
            }

            @Override
            public User getDirectoryUser() {
                return null;
            }

            @Override
            public Long getId() {
                return null;
            }
        };
    }

    public static IssueEvent buildIssueEventFromJson(JsonObject jsonObject) {
        final JsonObject issueJson = jsonObject.get("issue").getAsJsonObject();
        final JsonObject userJson = jsonObject.get("user").getAsJsonObject();
        final Issue issue = buildIssueFromJson(issueJson);
        final ApplicationUser user = buildUserFromJson(userJson);
        return new IssueEvent(
                issue,
                null,
                user,
                0L
        );
    }
}
