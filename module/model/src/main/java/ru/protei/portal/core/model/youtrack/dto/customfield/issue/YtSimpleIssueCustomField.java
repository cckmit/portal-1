package ru.protei.portal.core.model.youtrack.dto.customfield.issue;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-SimpleIssueCustomField.html
 */
public class YtSimpleIssueCustomField extends YtIssueCustomField {

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
