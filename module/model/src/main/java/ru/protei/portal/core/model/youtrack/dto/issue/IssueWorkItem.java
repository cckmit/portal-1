package ru.protei.portal.core.model.youtrack.dto.issue;

import ru.protei.portal.core.model.youtrack.annotation.YtEntityName;
import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;

@YtEntityName("IssueWorkItem")
public class IssueWorkItem extends YtDto {

    public YtUser author;
    public WorkItemType type;
    public DurationValue duration;
    public YtIssue issue;

    @Override
    public String toString() {
        return "YtIssueWorkItem{" +
                "id='" + id + '\'' +
                ", $type='" + $type + '\'' +
                ", author=" + author +
                ", type=" + type +
                ", duration=" + duration +
                '}';
    }
}
