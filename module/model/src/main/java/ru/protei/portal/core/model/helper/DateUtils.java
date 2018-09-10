package ru.protei.portal.core.model.helper;

import java.util.Date;

public class DateUtils {
    public static boolean beforeNotNull(Date d1, Date d2) {
        if (d1 == null || d2 == null)
            return false;
        return d1.before(d2);
    }
}
