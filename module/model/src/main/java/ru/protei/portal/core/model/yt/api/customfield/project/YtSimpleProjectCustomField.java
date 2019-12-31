package ru.protei.portal.core.model.yt.api.customfield.project;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-SimpleProjectCustomField.html */
public class YtSimpleProjectCustomField extends YtProjectCustomField {

    @Override
    public String toString() {
        return "YtSimpleProjectCustomField{" +
                "field=" + field +
                ", project=" + project +
                ", canBeEmpty=" + canBeEmpty +
                ", emptyFieldText='" + emptyFieldText + '\'' +
                '}';
    }
}
