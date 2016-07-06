package ru.protei.portal.core.utils;

import java.util.Date;

/**
 * Created by michael on 06.07.16.
 */
public class HelperFunc {

    public static Long toTime (Date t, Long v) {
        return t == null ? v : t.getTime();
    }

    public static Object nvl (Object...arr) {
        for (Object v : arr) {
            if (v != null)
                return v;
        }

        return null;
    }

}
