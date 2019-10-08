package ru.protei.portal.jira.utils;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.helper.JiraUtils;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class CommonUtils {

    private final static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static boolean isTechUser (JiraEndpoint endpoint, BasicUser user) {
        return user != null && user.getName().equals(endpoint.getServerLogin());
    }

    public static String makeExternalIssueID (JiraEndpoint endpoint, Issue issue) {
        return endpoint.getId()+ "_" + issue.getKey();
    }

//    public static String makeExtAppData (Issue issue) {
//        return String.valueOf(issue.getProject().getId());
//    }

    public static JiraUtils.JiraIssueData convert(ExternalCaseAppData appData) {
        return JiraUtils.convert(appData);
    }

    private static Map<String,URI> fakeAvatarURI_map = Collections.singletonMap(User.S48_48, safeURI("https://atlassian.com/"));

    private static URI safeURI (String uri) {
        try {
            return new URI(uri);
        }
        catch (Throwable e) {
            return null;
        }
    }

    public static User fromBasicUserInfo (BasicUser basicUser) {
        return new User(basicUser.getSelf(), basicUser.getDisplayName(), basicUser.getDisplayName(), null, true,
                null, fakeAvatarURI_map, null);
    }

    public static String getIssueSeverity (Issue issue) {
        IssueField field = issue.getFieldByName(CustomJiraIssueParser.SEVERITY_CODE_NAME);
        return field == null ? null : field.getValue().toString();
    }

//    // значение содержит строковое описание, из которого нужно получить число
//    private static Pattern pattern = Pattern.compile(".*([0-9]{2}).*");
//    public static String dirtyHackForSeverity (String value) {
//        Matcher m = pattern.matcher(value);
//        return m.matches() ? m.group(1) : "0";
//    }

//
//    private static final Map<String, En_CaseType> mappingOfTypes = new HashMap<String, En_CaseType>() {{
//        put("Improvement", En_CaseType.FREQ);
//        put("Error", En_CaseType.BUG);
//        put("Service", En_CaseType.TASK);
//        put("Consultation", En_CaseType.OFFICIAL);
//    }};

//    private static final Map<String, En_CaseState> mappingOfStatuses = new HashMap<String, En_CaseState>() {{
//        put("Authorized", En_CaseState.CREATED);
//        put("Studying", En_CaseState.OPENED);
//        put("Request to customer", En_CaseState.INFO_REQUEST);
//        put("Postpone", En_CaseState.PAUSED);
//        put("Soft close", En_CaseState.DONE);
//        put("Nothing to change", En_CaseState.VERIFIED);
//    }};
}
