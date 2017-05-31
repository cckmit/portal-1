package ru.protei.portal.core.model.helper;

import org.apache.commons.collections4.ComparatorUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

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


    public static boolean isNotEmpty (String s) {
        return !isEmpty(s);
    }

    public static boolean equals (Object o1, Object o2) {
        return o1 == null ? o2 == null : o2 != null ? (o1 == o2 || o1.equals(o2)) : false;
    }

    public static boolean allEquals (Object...objects) {
        if (objects == null || objects.length == 0)
            return true;

        Object cmp = objects[0];

        for (Object o : objects) {
            if (!equals(cmp, o))
                return false;
        }

        return true;
    }

    public static String joinNotEmpty (String delim, String... arr) {
        return Arrays.stream(arr).filter(s -> isNotEmpty(s)).collect(Collectors.joining(delim));
    }

    public static boolean testAllNotEmpty (String...arr) {
        for (String s : arr)
            if (isEmpty(s))
                return false;

        return true;
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

    public static boolean isLikeRequired (String arg) {
        return isNotEmpty(arg) && !arg.equals("%");
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
