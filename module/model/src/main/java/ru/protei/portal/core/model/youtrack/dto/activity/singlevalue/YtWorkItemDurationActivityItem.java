package ru.protei.portal.core.model.youtrack.dto.activity.singlevalue;

import ru.protei.portal.core.model.youtrack.dto.issue.DurationValue;
import ru.protei.portal.core.model.youtrack.dto.issue.IssueWorkItem;

public class YtWorkItemDurationActivityItem extends YtSimpleValueActivityItem {

    public DurationValue removed;
    public DurationValue added;
    public IssueWorkItem target;

    @Override
    public String toString() {
        return "YtWorkItemDurationActivityItem{" +
                "removed=" + removed +
                ", added=" + added +
                ", field=" + field +
                ", author=" + author +
                ", timestamp=" + timestamp +
                ", target=" + target +
                '}';
    }
}
