package ru.protei.portal.ui.common.client.util;

import java.util.Arrays;
import java.util.List;

public class TopBrassPersonIdsUtil {
    private static final List<Long> personIds = Arrays.asList(29L, 4L, 20L, 45L, 25L);

    public static List<Long> getPersonIds() {
        return personIds;
    }

    public static List<Long> getTopIds() {
        return Arrays.asList(29L, 4L);
    }

    public static List<Long> getBottomIds() {
        return Arrays.asList(20L, 45L, 25L);
    }
}
