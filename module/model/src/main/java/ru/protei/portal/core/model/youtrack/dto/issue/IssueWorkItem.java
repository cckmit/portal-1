package ru.protei.portal.core.model.youtrack.dto.issue;

import ru.protei.portal.core.model.youtrack.annotation.YtEntityName;
import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-Issue.html
 */
@YtEntityName("IssueWorkItem")
public class IssueWorkItem extends YtDto {

    public YtUser author;
    public YtUser creator;
    public String text;
    public String textPreview;
    public WorkItemType type;
    public Long created;
    public Long updated;
    public DurationValue duration;
    public Long date;

    @Override
    public String toString() {
        return "YtIssueWorkItem{" +
                "id='" + id + '\'' +
                ", $type='" + $type + '\'' +
                ", author=" + author +
                ", creator=" + creator +
                ", text='" + text + '\'' +
                ", textPreview='" + textPreview + '\'' +
                ", type=" + type +
                ", created=" + created +
                ", updated=" + updated +
                ", duration=" + duration +
                ", date=" + date +
                '}';
    }
}
