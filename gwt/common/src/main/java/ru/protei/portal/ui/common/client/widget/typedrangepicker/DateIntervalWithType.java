package ru.protei.portal.ui.common.client.widget.typedrangepicker;

import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_DateIntervalType;

public class DateIntervalWithType {
    private DateInterval interval;
    private En_DateIntervalType type;

    public DateIntervalWithType(DateInterval interval, En_DateIntervalType type) {
        this.interval = interval;
        this.type = type;
    }

    public DateInterval getInterval() {
        return interval;
    }

    public void setInterval(DateInterval interval) {
        this.interval = interval;
    }

    public En_DateIntervalType getIntervalType() {
        return type;
    }

    public void setIntervalType(En_DateIntervalType type) {
        this.type = type;
    }
}
