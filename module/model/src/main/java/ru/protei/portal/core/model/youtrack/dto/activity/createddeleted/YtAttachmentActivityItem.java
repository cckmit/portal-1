package ru.protei.portal.core.model.youtrack.dto.activity.createddeleted;

import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueAttachment;

import java.util.List;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-AttachmentActivityItem.html
 */
public class YtAttachmentActivityItem extends YtCreatedDeletedActivityItem {

    public List<YtIssueAttachment> removed;
    public List<YtIssueAttachment> added;

    @Override
    public String toString() {
        return "YtAttachmentActivityItem{" +
                "removed=" + removed +
                ", added=" + added +
                ", field=" + field +
                ", author=" + author +
                ", timestamp=" + timestamp +
                '}';
    }
}
