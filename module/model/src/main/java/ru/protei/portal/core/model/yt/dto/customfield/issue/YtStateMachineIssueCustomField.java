package ru.protei.portal.core.model.yt.dto.customfield.issue;

import ru.protei.portal.core.model.yt.dto.bundleelemenet.YtStateBundleElement;
import ru.protei.portal.core.model.yt.dto.customfield.project.YtStateProjectCustomField;

/** not documented */
public class YtStateMachineIssueCustomField extends YtSingleValueIssueCustomField {

    public YtStateProjectCustomField projectCustomField;
    public YtStateBundleElement value;
    @Override
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
        return "YtStateMachineIssueCustomField{" +
                "value=" + value +
                ", projectCustomField=" + projectCustomField +
                ", name='" + name + '\'' +
                '}';
    }
}
