package ru.protei.portal.core.model.yt.dto.filterfield;

import ru.protei.portal.core.model.yt.dto.YtDto;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-FilterField.html */
public class YtFilterField extends YtDto {

    public String presentation;
    public String name;

    @Override
    public String toString() {
        return "YtFilterField{" +
                "presentation='" + presentation + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
