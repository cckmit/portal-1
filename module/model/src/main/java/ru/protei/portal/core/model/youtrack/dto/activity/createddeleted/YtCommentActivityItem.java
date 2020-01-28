package ru.protei.portal.core.model.youtrack.dto.activity.createddeleted;

import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueComment;

import java.util.List;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-CommentActivityItem.html
 */
public class YtCommentActivityItem extends YtCreatedDeletedActivityItem {

    public List<YtIssueComment> removed;
    public List<YtIssueComment> added;

    @Override
    public String toString() {
        return "YtCommentActivityItem{" +
                "removed=" + removed +
                ", added=" + added +
                ", field=" + field +
                ", author=" + author +
                ", timestamp=" + timestamp +
                '}';
    }
}
