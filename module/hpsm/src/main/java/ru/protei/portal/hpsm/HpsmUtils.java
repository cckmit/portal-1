package ru.protei.portal.hpsm;

import protei.utils.common.ThreadLocalDateFormat;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by michael on 25.04.17.
 */
public class HpsmUtils {
    public static final ThreadLocalDateFormat DATE_FMT = new ThreadLocalDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String formatDate(Date date) {
        return date == null ? "" : DATE_FMT.format(date);
    }

    public static Date parseDate(String x) throws ParseException{
        return x == null || x.isEmpty() ? null : DATE_FMT.parse(x);
    }


    public static String extractOption (String x, String name, String defaultValue) {
        if (x == null || x.isEmpty())
            return defaultValue;

        String tag = name+"=[";
        int tag_len = tag.length();

        int s = x.indexOf(tag);
        if (s < 0)
            return defaultValue;

        int e = x.indexOf(']', s + tag_len);

        if (e < 0)
            return defaultValue;

        return x.substring(s+tag_len, e);
    }
}
