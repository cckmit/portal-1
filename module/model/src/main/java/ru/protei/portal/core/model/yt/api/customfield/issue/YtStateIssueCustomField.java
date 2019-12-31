package ru.protei.portal.core.model.yt.api.customfield.issue;

import ru.protei.portal.core.model.yt.api.bundleelemenet.YtStateBundleElement;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-StateIssueCustomField.html */
public class YtStateIssueCustomField extends YtSingleValueIssueCustomField {

    public YtStateBundleElement value;
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
        return "YtStateIssueCustomField{" +
                "value=" + value +
                ", projectCustomField=" + projectCustomField +
                ", name='" + name + '\'' +
                '}';
    }
}
