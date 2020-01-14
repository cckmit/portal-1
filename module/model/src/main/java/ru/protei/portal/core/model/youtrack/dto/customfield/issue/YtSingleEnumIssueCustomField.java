package ru.protei.portal.core.model.youtrack.dto.customfield.issue;

import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtEnumBundleElement;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-SingleEnumIssueCustomField.html
 */
public class YtSingleEnumIssueCustomField extends YtSingleValueIssueCustomField {

    @YtAlwaysInclude
    public YtEnumBundleElement value;

    public String getValue() {
        if (value == null) {
            return null;
        }
        if (StringUtils.isNotEmpty(value.localizedName)) {
            return value.localizedName;
        }
        if (StringUtils.isNotEmpty(value.name)) {
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
