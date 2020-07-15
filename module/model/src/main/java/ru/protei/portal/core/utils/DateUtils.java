package ru.protei.portal.core.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.core.model.util.CrmConstants.Time.DAY;

public class DateUtils {

    public static boolean beforeNotNull(Date d1, Date d2) {
        if (d1 == null || d2 == null)
            return false;
        return d1.before(d2);
    }

    public static Date max(Date... dates) {
        return Arrays.stream(dates)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);
    }

    public static Date resetSeconds(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

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
