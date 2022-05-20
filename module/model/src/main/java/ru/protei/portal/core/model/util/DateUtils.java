package ru.protei.portal.core.model.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DateUtils {

    private static final long MILLIS_PER_DAY = 86400000L;

    public static Date max(Date... dates) {
        return Arrays.stream(dates)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);
    }

    public static Date resetSeconds(Date date) {
        date.setSeconds(0);
        setMilliseconds(date, 0);
        return date;
    }

    public static Date resetTime(Date date) {
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        setMilliseconds(date, 0);
        return date;
    }

    public static long daysBetween(Date firstDate, Date secondDate) {
        return Math.abs(secondDate.getTime() - firstDate.getTime())/MILLIS_PER_DAY;
    }

    public static Date addDay(Date date) {
        return new Date(date.getTime() + MILLIS_PER_DAY);
    }

    public static boolean isSameDay(Date from, Date to) {
        if (from == null || to == null) return false;
        return from.getYear() == to.getYear() && from.getMonth() == to.getMonth() && from.getDate() == to.getDate();
    }

    public static boolean isSameMonth(Date from, Date to) {
        if (from == null || to == null) return false;
        return from.getMonth() == to.getMonth();
    }

    public static boolean isSameYear(Date from, Date to) {
        if (from == null || to == null) return false;
        return from.getYear() == to.getYear();
    }

    private static void setMilliseconds(Date date, long millis) {
        date.setTime(date.getTime() / 1000L * 1000L + millis);
    }

    public static int compareOnlyDate(Date from, Date to) {
        if (from.getYear() < to.getYear())
            return -1;
        else if (from.getYear() > to.getYear())
            return 1;
        if (from.getMonth() < to.getMonth())
            return -1;
        else if (from.getMonth() > to.getMonth())
            return 1;
        else if (from.getDate() > to.getDate())
            return 1;
        if (from.getDate() < to.getDate())
            return -1;
        return 0;
    }
}
