package ru.protei.portal.core.model.youtrack.dto.customfield.issue;

import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;

import java.util.Date;

/**
 * https://www.jetbrains.com/help/youtrack/devportal/api-entity-DateIssueCustomField.html
 */
public class YtDateIssueCustomField extends YtIssueCustomField {

    @YtAlwaysInclude
    public Date value;

    public Date getValue() { return value; }

    @Override
    public String toString() {
        return "YtDateIssueCustomField{" +
                "value='" + value + '\'' +
                ", projectCustomField=" + projectCustomField +
                ", name='" + name + '\'' +
                '}';
    }
}
