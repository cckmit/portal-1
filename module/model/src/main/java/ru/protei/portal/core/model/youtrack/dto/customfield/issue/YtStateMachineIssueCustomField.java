package ru.protei.portal.core.model.youtrack.dto.customfield.issue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtStateBundleElement;
import ru.protei.portal.core.model.youtrack.dto.customfield.project.YtStateProjectCustomField;

/**
 * not documented
 */
public class YtStateMachineIssueCustomField extends YtSingleValueIssueCustomField {

    public YtStateProjectCustomField projectCustomField;
    @YtAlwaysInclude
    public YtStateBundleElement value;

    @JsonIgnore
    public String getValueAsString() {
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
        return "YtStateMachineIssueCustomField{" +
                "value=" + value +
                ", projectCustomField=" + projectCustomField +
                ", name='" + name + '\'' +
                '}';
    }
}
