package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.struct.Interval;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateRangeUtils {

    public static Interval makeToday() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now();
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        interval.to = Date.from(local.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    public static  Interval makeYesterday() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now();
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        interval.from = Date.from(local.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    public static Interval makeThisWeek() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue());
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        interval.to = Date.from(local.plusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    public static Interval makeLastWeek() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue());
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        interval.from = Date.from(local.minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    public static Interval makeThisMonth() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        interval.to = Date.from(local.plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    public static Interval makeLastMonth() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        interval.from = Date.from(local.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    public static  Interval makeThisYear() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfYear()-1);
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        interval.to = Date.from(local.plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    public static  Interval makeLastYear() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfYear()-1);
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        interval.from = Date.from(local.minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }
}
