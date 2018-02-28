package ru.protei.portal.redmine.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class RedmineUtils {
    //Somewhy datetime in issues stored in GMT timezone, therefore we need -3 hours from our time
    public static String parseDateToAfter(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -3);
        return AFTER + dateTimeFormatter.format(calendar.getTime()) + "Z";
    }

    private static final String AFTER = ">=";
    private static final Format dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
}
