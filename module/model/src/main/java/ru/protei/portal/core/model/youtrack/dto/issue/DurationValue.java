package ru.protei.portal.core.model.youtrack.dto.issue;

import ru.protei.portal.core.model.youtrack.annotation.YtEntityName;
import ru.protei.portal.core.model.youtrack.dto.YtDto;

@YtEntityName("DurationValue")
public class DurationValue extends YtDto {
    public Integer minutes;
    public String presentation;

    @Override
    public String toString() {
        return "DurationValue{" +
                "id='" + id + '\'' +
                ", $type='" + $type + '\'' +
                ", minutes=" + minutes +
                ", presentation='" + presentation + '\'' +
                '}';
    }
}
