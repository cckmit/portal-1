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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class JiraHookEventData {

    private static Logger logger = LoggerFactory.getLogger(JiraHookEventData.class);


    private long timestamp;
    private JiraHookEventType eventType;
    private User user;
    private Issue issue;

    private Long changelogId;
    private Collection<ChangelogItem> changelogItems;
    private Comment comment;


    public JiraHookEventData() {
        this (System.currentTimeMillis(), null);
    }

    public JiraHookEventData(long timestamp, JiraHookEventType eventType) {
        this.timestamp = timestamp;
        this.eventType = eventType;
    }

    public boolean isCreateIssueEvent () {
        return this.eventType == JiraHookEventType.ISSUE_CREATED;
    }

    public boolean isUpdateIssueEvent () {
        return this.eventType == JiraHookEventType.ISSUE_UPDATED;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JiraHookEventType getEventType() {
        return eventType;
    }

    public User getUser() {
        return user;
    }

    public Issue getIssue() {
        return issue;
    }

    public Long getChangelogId() {
        return changelogId;
    }

    public Collection<ChangelogItem> getChangelogItems() {
        return changelogItems;
    }

    public Comment getComment() {
        return comment;
    }

    public String toDebugString () {

        return "jira-event: " + this.eventType.name() + "/" + this.timestamp +
                "[user=" + (user != null ? user.getDisplayName() : "-")
                +", issue=" + (issue != null ? issue.getKey() : "-")
                +", log-id=" + (changelogId != null ? changelogId : "-")
                +", log-items-count=" + (changelogItems != null ? changelogItems.size() : 0)
                +", comment=" + (comment != null ? comment.getBody() : "-")
                + "]";
    }


    public static JiraHookEventData parse (String val) throws JSONException {
        JSONObject jsonObject = new JSONObject(val);
        String etype = jsonObject.getString("webhookEvent");
        JiraHookEventType eventType = JiraHookEventType.byCode(etype);
        if (eventType == null) {
            logger.debug("unknown event type: {}", etype);
            return null;
        }

        JiraHookEventData data = new JiraHookEventData(jsonObject.getLong("timestamp"), eventType);

        data.user = JsonParseUtil.parseOptionalJsonObject(jsonObject, "user", new UserJsonParser());
        data.issue = JsonParseUtil.parseOptionalJsonObject(jsonObject, "issue", new CustomJiraIssueParser());

        data.comment = JsonParseUtil.parseOptionalJsonObject(jsonObject, "comment", new CommentJsonParser());
        if (jsonObject.has("changelog")) {
            JSONObject chLogJson = jsonObject.getJSONObject("changelog");

            data.changelogId = JsonParseUtil.getOptionalLong(chLogJson, "id");
            data.changelogItems = JsonParseUtil.parseJsonArray(chLogJson.getJSONArray("items"), new ChangelogItemJsonParser());
        }

        return data;
    }
}
