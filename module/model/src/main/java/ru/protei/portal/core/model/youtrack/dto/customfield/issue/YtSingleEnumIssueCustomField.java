package ru.protei.portal.core.model.youtrack.dto.customfield.issue;

import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtEnumBundleElement;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-SingleEnumIssueCustomField.html
 */
public class YtSingleEnumIssueCustomField extends YtSingleValueIssueCustomField {

    public YtEnumBundleElement value;
    public String getValue() {
        if (value == null) {
            return null;
        }
        if (value.localizedName != null) {
            return value.localizedName;
        }
        if (value.name != null) {
            return value.name;
        }
        return null;
    }


    @Override
    public String toString() {
        return "YtSingleEnumIssueCustomField{" +
                "value=" + value +
                ", projectCustomField=" + projectCustomField +
                ", name='" + name + '\'' +
                '}';
    }
}
