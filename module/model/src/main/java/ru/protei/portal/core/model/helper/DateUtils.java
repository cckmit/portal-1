package ru.protei.portal.core.model.helper;

import java.util.Arrays;
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
}
