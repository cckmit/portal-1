package ru.protei.portal.ui.common.client.util;

import ru.protei.portal.core.model.util.CrmConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopBrassPersonUtils {
    private static final List<Long> topIds;
    private static final List<Long> bottomIds;
    private static final List<Long> personIds;

    static {
        topIds = Arrays.asList(
                CrmConstants.TopBrassPerson.PINCHUK_PERSON_ID,
                CrmConstants.TopBrassPerson.APOSTOLOVA_PERSON_ID);
        bottomIds = Arrays.asList(
                CrmConstants.TopBrassPerson.KOLOBKOV_PERSON_ID,
                CrmConstants.TopBrassPerson.FREYKMAN_PERSON_ID,
                CrmConstants.TopBrassPerson.MASLOV_PERSON_ID);

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
