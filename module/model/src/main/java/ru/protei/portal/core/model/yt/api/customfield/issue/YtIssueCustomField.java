package ru.protei.portal.core.model.yt.api.customfield.issue;

import ru.protei.portal.core.model.yt.api.YtDto;
import ru.protei.portal.core.model.yt.api.customfield.project.YtProjectCustomField;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-IssueCustomField.html */
public abstract class YtIssueCustomField extends YtDto {
    public YtProjectCustomField projectCustomField;
    public String name;
    public abstract String getValue();
}
