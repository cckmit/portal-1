package ru.protei.portal.core.model.helper;

import java.util.Date;

import static ru.protei.portal.core.model.util.CrmConstants.Time.DAY;

public class DateUtils {

    public static Long getDaysBetween(Date from, Date until) {
        if (from == null || until == null) {
            return null;
        }
        long fromTs = from.getTime();
        long untilTs = until.getTime();
        long diffTs = untilTs - fromTs;
        long days = diffTs / DAY;
        return days;
    }

    public static Date addDays(Date base, Long days) {
        if (base == null || days == null) {
            return null;
        }
        long daysMs = days * DAY;
        long diffTs = base.getTime() + daysMs;
        return new Date(diffTs);
    }
}
