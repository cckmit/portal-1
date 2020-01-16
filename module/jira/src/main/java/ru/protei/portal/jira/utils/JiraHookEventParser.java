package ru.protei.portal.jira.utils;

import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.json.ChangelogItemJsonParser;
import com.atlassian.jira.rest.client.internal.json.CommentJsonParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.atlassian.jira.rest.client.internal.json.UserJsonParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import ru.protei.portal.jira.dict.JiraHookEventType;
import ru.protei.portal.jira.dto.JiraHookEventData;

import java.util.Collection;

public class JiraHookEventParser {

    public static JiraHookEventData parse(String val) throws JSONException {

        JSONObject json = new JSONObject(val);

        JiraHookEventType eventType = getEventType(json);
        if (eventType == null) {
            return null;
        }

        JiraHookEventData data = new JiraHookEventData(getTimestamp(json), eventType);
        data.setUser(getUser(json));
        data.setIssue(getIssue(json));
        data.setComment(getComment(json));
        if (json.has("changelog")) {
            data.setChangelogId(getChangeLogId(json));
            data.setChangelogItems(getChangeLogItems(json));
        }

        return data;
    }

    private static JiraHookEventType getEventType(JSONObject json) throws JSONException {
        String type = json.getString("webhookEvent");
        return JiraHookEventType.byCode(type);
    }

    private static long getTimestamp(JSONObject json) throws JSONException {
        return json.getLong("timestamp");
    }

    private static User getUser(JSONObject json) throws JSONException {
        return JsonParseUtil.parseOptionalJsonObject(json, "user", new UserJsonParser());
    }

    private static Issue getIssue(JSONObject json) throws JSONException {
        return JsonParseUtil.parseOptionalJsonObject(json, "issue", new CustomJiraIssueParser());
    }

    private static Comment getComment(JSONObject json) throws JSONException {
        return JsonParseUtil.parseOptionalJsonObject(json, "comment", new CommentJsonParser());
    }

    private static Long getChangeLogId(JSONObject json) throws JSONException {
        JSONObject chLogJson = json.getJSONObject("changelog");
        return JsonParseUtil.getOptionalLong(chLogJson, "id");
    }

    private static Collection<ChangelogItem> getChangeLogItems(JSONObject json) throws JSONException {
        JSONObject chLogJson = json.getJSONObject("changelog");
        return JsonParseUtil.parseJsonArray(chLogJson.getJSONArray("items"), new ChangelogItemJsonParser());
    }
}
