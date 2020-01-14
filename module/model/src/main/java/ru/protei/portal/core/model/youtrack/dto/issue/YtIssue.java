package ru.protei.portal.core.model.youtrack.dto.issue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.youtrack.annotation.YtDtoFieldAlwaysInclude;
import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;

import java.util.List;
import java.util.Objects;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-Issue.html
 */
public class YtIssue extends YtDto {

    @YtDtoFieldAlwaysInclude
    public String idReadable;
    public String summary;
    public String description;
    public YtUser reporter;
    public YtUser updater;
    public YtProject project;
    public List<YtIssueCustomField> customFields;
    public List<YtIssueComment> comments;
    public List<YtIssueAttachment> attachments;

    @JsonIgnore
    public YtIssueCustomField getField(String fieldName) {
        return CollectionUtils.stream(customFields)
                .filter(Objects::nonNull)
                .filter((field) -> Objects.equals(fieldName, field.name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "YtIssue{" +
                "idReadable='" + idReadable + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", reporter=" + reporter +
                ", updater=" + updater +
                ", project=" + project +
                ", customFields=" + customFields +
                ", comments=" + comments +
                ", attachments=" + attachments +
                '}';
    }
}
