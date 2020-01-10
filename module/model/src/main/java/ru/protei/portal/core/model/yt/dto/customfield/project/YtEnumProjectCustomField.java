package ru.protei.portal.core.model.yt.dto.customfield.project;

import ru.protei.portal.core.model.yt.dto.bundle.YtEnumBundle;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-EnumProjectCustomField.html */
public class YtEnumProjectCustomField extends YtBundleProjectCustomField {

    public YtEnumBundle bundle;

    @Override
    public String toString() {
        return "YtEnumProjectCustomField{" +
                "bundle=" + bundle +
                ", field=" + field +
                ", project=" + project +
                ", canBeEmpty=" + canBeEmpty +
                ", emptyFieldText='" + emptyFieldText + '\'' +
                '}';
    }
}
