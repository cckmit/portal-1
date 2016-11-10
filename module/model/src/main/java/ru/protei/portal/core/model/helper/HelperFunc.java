package ru.protei.portal.core.model.helper;

import java.util.Date;

/**
 * Created by michael on 06.07.16.
 */
public class HelperFunc {

    public static Long toTime (Date t, Long v) {
        return t == null ? v : t.getTime();
    }

    public static boolean isEmpty (String s) {
        return s == null || s.trim().isEmpty();
    }

    public static <T> T nvlt (T...arr) {
        for (T t : arr)
            if (t != null)
                return t;
        return null;
    }

    public static Object nvl (Object...arr) {
        for (Object v : arr) {
            if (v != null)
                return v;
        }

        return null;
    }


    public static String makeLikeArg (String arg) {
        return makeLikeArg (arg, false);
    }

    public static String makeLikeArg (String arg, boolean leftSideAny) {
        if (arg == null || arg.isEmpty()) {
            return "%";
        }

        if (leftSideAny && !arg.startsWith("%"))
            arg = "%" + arg;

        if (!arg.endsWith("%"))
            arg = arg + "%";

        return arg;
    }
}
