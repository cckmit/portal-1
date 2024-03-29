package ru.protei.portal.core.model.youtrack.dto.customfield.issue;

import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-SimpleIssueCustomField.html
 */
public class YtSimpleIssueCustomField extends YtIssueCustomField {

    @YtAlwaysInclude
    public String value;

    public String getValue() { return value; }

    @Override
    public String toString() {
        return "YtSimpleIssueCustomField{" +
                "value='" + value + '\'' +
                ", projectCustomField=" + projectCustomField +
                ", name='" + name + '\'' +
                '}';
    }
}
