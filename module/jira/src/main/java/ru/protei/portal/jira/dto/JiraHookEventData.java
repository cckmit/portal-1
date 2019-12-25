package ru.protei.portal.jira.dto;

import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.User;
import ru.protei.portal.jira.dict.JiraHookEventType;

import java.util.Collection;

public class JiraHookEventData {

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

    public JiraHookEventData(JiraHookEventType eventType, Issue issue) {
        this (System.currentTimeMillis(), eventType);
        this.issue = issue;
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

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setChangelogId(Long changelogId) {
        this.changelogId = changelogId;
    }

    public Long getChangelogId() {
        return changelogId;
    }

    public void setChangelogItems(Collection<ChangelogItem> changelogItems) {
        this.changelogItems = changelogItems;
    }

    public Collection<ChangelogItem> getChangelogItems() {
        return changelogItems;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
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

    public String toFullString() {
        return "jira-event: " + this.eventType.name() + "/" + this.timestamp
                + "[user=" + user
                + ", issue=" + issue
                + ", log-id=" + changelogId
                + ", log-items-count=" + (changelogItems != null ? changelogItems.size() : 0)
                + ", log-items=" + changelogItems
                + ", comment=" + comment
                + "]";
    }
}
