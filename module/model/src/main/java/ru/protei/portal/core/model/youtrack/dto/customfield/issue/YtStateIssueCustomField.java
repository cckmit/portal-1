package ru.protei.portal.core.model.youtrack.dto.customfield.issue;

import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtStateBundleElement;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-StateIssueCustomField.html
 */
public class YtStateIssueCustomField extends YtSingleValueIssueCustomField {

    @YtAlwaysInclude
    public YtStateBundleElement value;

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
        return "YtStateIssueCustomField{" +
                "value=" + value +
                ", projectCustomField=" + projectCustomField +
                ", name='" + name + '\'' +
                '}';
    }
}
