package ru.protei.portal.jira.utils;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.User;
import ru.protei.portal.core.model.ent.JiraEndpoint;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class CommonUtils {

    public static boolean isTechUser (JiraEndpoint endpoint, BasicUser user) {
        return user != null && user.getName().equals(endpoint.getServerLogin());
    }

    public static String makeExternalIssueID (JiraEndpoint endpoint, Issue issue) {
        return endpoint.getId()+ "_" + issue.getKey();
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
}
