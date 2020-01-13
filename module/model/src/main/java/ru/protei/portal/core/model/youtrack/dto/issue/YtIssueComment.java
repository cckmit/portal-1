package ru.protei.portal.core.model.youtrack.dto.issue;

import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;

import java.util.List;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-IssueComment.html
 */
public class YtIssueComment extends YtDto {

    public String text;
    public String textPreview;
    public Boolean usesMarkdown;
    public Long created;
    public Long updated;
    public Boolean deleted;
    public YtUser author;
    public List<YtIssueAttachment> attachments;

    @Override
    public String toString() {
        return "YtIssueComment{" +
                "text='" + text + '\'' +
                ", textPreview='" + textPreview + '\'' +
                ", usesMarkdown=" + usesMarkdown +
                ", created=" + created +
                ", updated=" + updated +
                ", deleted=" + deleted +
                ", author=" + author +
                ", attachments=" + attachments +
                '}';
    }
}
