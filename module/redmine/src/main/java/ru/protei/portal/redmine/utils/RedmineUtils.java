package ru.protei.portal.redmine.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RedmineUtils {
    public static String parseDateToAfter(Date date) {
        return AFTER + dateTimeFormatter.format(date);
    }

    public static String parseDateToBefore(Date date) {
        return BEFORE + dateTimeFormatter.format(date);
    }

    public static String parseDateToRange(Date start, Date end) {
        return RANGE + dateTimeFormatter.format(start) + RANGE_SEPARATOR + dateTimeFormatter.format(end);
    }


    private static final String AFTER = ">=";
    private static final String BEFORE = "<=";
    private static final String RANGE = "><";
    private static final String RANGE_SEPARATOR = "|";


    private static final Format dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final Format dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
}
