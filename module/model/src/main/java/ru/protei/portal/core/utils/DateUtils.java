package ru.protei.portal.core.utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

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
}