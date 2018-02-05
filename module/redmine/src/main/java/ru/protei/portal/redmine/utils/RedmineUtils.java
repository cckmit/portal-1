package ru.protei.portal.redmine.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RedmineUtils {
    public static String parseDateToAfter(Date date) {
        return AFTER + formatter.format(date);
    }

    public static String parseDateToBefore(Date date) {
        return BEFORE + formatter.format(date);
    }

    public static String parseDateToRange(Date start, Date end) {
        return RANGE + formatter.format(start) + RANGE_SEPARATOR + formatter.format(end);
    }


    private static final String AFTER = ">=";
    private static final String BEFORE = "<=";
    private static final String RANGE = "><";
    private static final String RANGE_SEPARATOR = "|";


    private static final Format formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
}
