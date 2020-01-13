package ru.protei.portal.core.model.youtrack.dto.filterfield;

import ru.protei.portal.core.model.youtrack.dto.customfield.YtCustomField;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-CustomFilterField.html
 */
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
