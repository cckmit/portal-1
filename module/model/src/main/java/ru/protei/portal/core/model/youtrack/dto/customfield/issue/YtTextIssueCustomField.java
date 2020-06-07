package ru.protei.portal.core.model.youtrack.dto.customfield.issue;

import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;
import ru.protei.portal.core.model.youtrack.dto.value.YtTextFieldValue;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-TextIssueCustomField.html
 */
public class YtTextIssueCustomField extends YtIssueCustomField{

    @YtAlwaysInclude
    public YtTextFieldValue value;

    public YtTextFieldValue getValue() { return value; }

    @Override
    public String toString() {
        return "YtTextIssueCustomField{" +
                "value='" + value + '\'' +
                ", projectCustomField=" + projectCustomField +
                ", name='" + name + '\'' +
                '}';
    }
}
