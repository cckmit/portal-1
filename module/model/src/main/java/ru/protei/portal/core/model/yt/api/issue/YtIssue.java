package ru.protei.portal.core.model.yt.api.issue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.yt.api.YtDto;
import ru.protei.portal.core.model.yt.api.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.yt.api.user.YtUser;
import ru.protei.portal.core.model.yt.fields.YtFields;

import java.util.List;
import java.util.Objects;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-Issue.html */
public class YtIssue extends YtDto {

    public String idReadable;
    public String summary;
    public String description;
    public YtUser reporter;
    public YtUser updater;
    public List<YtIssueCustomField> customFields;
    public List<YtIssueComment> comments;

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
                ", customFields=" + customFields +
                ", comments=" + comments +
                '}';
    }
}
