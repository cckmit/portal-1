package ru.protei.portal.core.model.yt.dto.filterfield;

import ru.protei.portal.core.model.yt.dto.customfield.YtCustomField;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-CustomFilterField.html */
public class YtCustomFilterField extends YtFilterField {

    public YtCustomField customField;

    @Override
    public String toString() {
        return "CustomFilterField{" +
                "presentation='" + presentation + '\'' +
                ", name='" + name + '\'' +
                ", customField=" + customField +
                '}';
    }
}
