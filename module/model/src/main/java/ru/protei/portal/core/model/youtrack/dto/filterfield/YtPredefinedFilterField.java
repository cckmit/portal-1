package ru.protei.portal.core.model.youtrack.dto.filterfield;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-PredefinedFilterField.html */
public class YtPredefinedFilterField extends YtFilterField {

    @Override
    public String toString() {
        return "YtPredefinedFilterField{" +
                "presentation='" + presentation + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
