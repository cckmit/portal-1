package ru.protei.portal.jira.utils;

import com.atlassian.jira.rest.client.api.domain.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.JiraEndpoint;

import java.util.HashMap;
import java.util.Map;

public class CommonUtils {
    private final static Logger logger = LoggerFactory.getLogger(CommonUtils.class);


    public static String makeExternalIssueID (JiraEndpoint endpoint, Issue issue) {
        return endpoint.getId()+ "_" + issue.getKey();
    }

    public static String makeExtAppData (Issue issue) {
        return String.valueOf(issue.getProject().getId());
    }

    public static String extractIssueKey (String ourStoredId) {
        return ourStoredId.substring(ourStoredId.indexOf('_')+1);
    }

    public static String extractIssueKey (ExternalCaseAppData appData) {
        return extractIssueKey(appData.getExtAppCaseId());
    }

    public static String extractIssueProjectId (ExternalCaseAppData appData) {
        return appData.getExtAppData();
    }

    public static long extractEndpointId (ExternalCaseAppData appData) {
        return Long.parseLong(appData.getExtAppCaseId().substring(0, appData.getExtAppCaseId().indexOf('_')), 10);
    }

    public static IssueData convert (ExternalCaseAppData appData) {
        return new IssueData(extractEndpointId(appData), extractIssueKey(appData), extractIssueProjectId(appData));
    }

    public static class IssueData {
        public long endpointId;
        public String key;
        public String projectId;

        public IssueData(long endpointId, String key, String projectId) {
            this.endpointId = endpointId;
            this.key = key;
            this.projectId = projectId;
        }
    }

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


//    public static Issue convertPortalIssueToJiraIssue(CaseObject caseObject) {
//        final String extAppType = caseObject.getExtAppType();
//        final String[] splitted = extAppType.split("_");
//        if (splitted.length != 2)
//            return null;
//
//        final long issueId = Long.parseLong(splitted[1]);
//
//        final ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("protei_tech_user");
//        IssueService issueService = ComponentAccessor.getIssueService();
//        final Issue issue = issueService.getIssue(user, issueId).getIssue();
//        //retrieved issue
//
//        return issue;
//    }

    public static CaseObject updatePortalIssue(Issue issue, CaseObject obj, JiraEndpoint endpoint) {
        obj.setModified(issue.getUpdateDate().toDate());
        obj.setExtAppType("jira_nexign");
        issue.getPriority();
        issue.getStatus().getName();
        obj.setStateId(0);
//        obj.setState(En_CaseState.ACTIVE);
        obj.setName(issue.getSummary());
        obj.setInfo(issue.getDescription());
        obj.setLocal(0);
        obj.setInitiatorCompanyId(endpoint.getCompanyId());
        return obj;
    }

//    private static Issue buildIssueFromJson(JsonObject jsonObject) {
//        final Long id = jsonObject.get("id").getAsLong();
//        final JsonObject fields = jsonObject.get("fields").getAsJsonObject();
//
//        final String created = fields.get("created").getAsString();
//        final String updated = fields.get("updated").getAsString();
//        final String summary = fields.get("summary").getAsString();
//        final String description = fields.get("description").getAsString();
//        final String statusName = fields.get("status").getAsJsonObject().get("name").getAsString();
//        final Long projId = fields.get("project").getAsJsonObject().get("id").getAsLong();
//        final String severityId = fields.get("customfield_12405").getAsJsonObject().get("value").getAsString().split("-")[0].trim();
//        return new ShortenedIssue(
//                id,
//                created,
//                updated,
//                summary,
//                description,
//                statusName,
//                projId,
//                severityId
//        );
//    }

//    private static Map buildIssueParamsFromJson(JsonObject jsonObject) {
//        return null;
//    }

//    private static ApplicationUser buildUserFromJson(JsonObject jsonObject) {
//        return new ApplicationUser() {
//            @Override
//            public String getKey() {
//                return jsonObject.get("key").getAsString();
//            }
//
//            @Override
//            public String getUsername() {
//                return jsonObject.get("name").getAsString();
//            }
//
//            @Override
//            public String getName() {
//                return jsonObject.get("displayName").getAsString();
//            }
//
//            @Override
//            public long getDirectoryId() {
//                return 0;
//            }
//
//            @Override
//            public boolean isActive() {
//                return jsonObject.get("active").getAsBoolean();
//            }
//
//            @Override
//            public String getEmailAddress() {
//                return jsonObject.get("emailAddress").getAsString();
//            }
//
//            @Override
//            public String getDisplayName() {
//                return jsonObject.get("displayName").getAsString();
//            }
//
//            @Override
//            public User getDirectoryUser() {
//                return null;
//            }
//
//            @Override
//            public Long getId() {
//                return null;
//            }
//        };
//    }
//
//    public static IssueEvent buildIssueEventFromJson(JsonObject jsonObject) {
//        final JsonObject issueJson = jsonObject.get("issue").getAsJsonObject();
//        final JsonObject userJson = jsonObject.get("user").getAsJsonObject();
//        final Issue issue = buildIssueFromJson(issueJson);
//        final ApplicationUser user = buildUserFromJson(userJson);
//        return new IssueEvent(
//                issue,
//                null,
//                user,
//                0L
//        );
//    }
}
