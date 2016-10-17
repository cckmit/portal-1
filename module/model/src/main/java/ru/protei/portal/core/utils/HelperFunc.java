package ru.protei.portal.core.utils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
