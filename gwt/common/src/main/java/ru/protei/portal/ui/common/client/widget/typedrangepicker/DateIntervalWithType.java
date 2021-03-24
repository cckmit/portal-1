package ru.protei.portal.ui.common.client.widget.typedrangepicker;

import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.struct.DateRange;

import java.io.Serializable;
import java.util.Objects;

public class DateIntervalWithType implements Serializable {
    private DateInterval interval;
    private En_DateIntervalType type;

    public DateIntervalWithType() {}

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

    public static DateRange toDateRange(DateIntervalWithType dateInterval) {
        return toDateRange(dateInterval, null);
    }

    public static DateRange toDateRange(DateIntervalWithType dateInterval, Integer hourOffset) {
        En_DateIntervalType intervalType = dateInterval.getIntervalType();

        if (intervalType != null) {

            if (Objects.equals(intervalType, En_DateIntervalType.FIXED)) {
                DateInterval interval = dateInterval.getInterval();
                return new DateRange(intervalType, interval.from, interval.to);
            }

            return new DateRange(intervalType, hourOffset);
        }

        return null;
    }


    public static DateIntervalWithType fromDateRange(DateRange range) {
        if(range != null) {
            if(range.getFrom() != null || range.getTo() != null) {
                return new DateIntervalWithType(new DateInterval(range.getFrom(), range.getTo()), En_DateIntervalType.FIXED);
            } else {
                return new DateIntervalWithType( null, range.getIntervalType());
            }
        } else {
            return null;
        }
    }
}
