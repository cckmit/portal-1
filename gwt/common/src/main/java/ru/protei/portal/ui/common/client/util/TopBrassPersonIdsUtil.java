package ru.protei.portal.ui.common.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopBrassPersonIdsUtil {
    private static final List<Long> topIds;
    private static final List<Long> bottomIds;
    private static final List<Long> personIds;

    static {
        topIds = Arrays.asList(29L, 4L);
        bottomIds = Arrays.asList(20L, 45L, 25L);

        List<Long> allPersonIds = new ArrayList<>();
        allPersonIds.addAll(topIds);
        allPersonIds.addAll(bottomIds);

        personIds = allPersonIds;
    }

    public static List<Long> getPersonIds() {
        return personIds;
    }

    public static List<Long> getTopIds() {
        return topIds;
    }

    public static List<Long> getBottomIds() {
        return bottomIds;
    }
}
