package ru.protei.portal.core.model.youtrack.dto.activity.multivalue;

import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueAttachment;

import java.util.List;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-CommentAttachmentsActivityItem.html */
public class YtCommentAttachmentsActivityItem extends YtMultiValueActivityItem {

    public List<YtIssueAttachment> removed;
    public List<YtIssueAttachment> added;

    @Override
    public String toString() {
        return "YtCommentAttachmentsActivityItem{" +
                "removed=" + removed +
                ", added=" + added +
                ", field=" + field +
                ", author=" + author +
                ", timestamp=" + timestamp +
                '}';
    }
}
