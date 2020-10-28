package ru.protei.portal.core.model.dict;

import org.apache.commons.collections4.collection.UnmodifiableCollection;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.helper.CollectionUtils.unmodifiableListOf;

public enum En_DateIntervalType {
    MONTH,
    FIXED,
    UNLIMITED,
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    THIS_WEEK_AND_BEYOND,
    LAST_WEEK,
    NEXT_WEEK,
    THIS_MONTH,
    LAST_MONTH,
    PREVIOUS_AND_THIS_MONTH,
    NEXT_MONTH,
    THIS_YEAR,
    LAST_YEAR;

    private static List<En_DateIntervalType> defaultTypes;
    private static List<En_DateIntervalType> dutyTypes;
    private static List<En_DateIntervalType> reservedIpRangeTypes;
    private static List<En_DateIntervalType> issueRangeTypes;
    private static List<En_DateIntervalType> reportRangeTypes;

    static {
        defaultTypes = unmodifiableListOf(
                FIXED,
                TODAY,
                YESTERDAY,
                THIS_WEEK,
                LAST_WEEK,
                THIS_MONTH,
                LAST_MONTH,
                THIS_YEAR,
                LAST_YEAR
        );

        reservedIpRangeTypes = unmodifiableListOf( MONTH, FIXED, UNLIMITED );

        issueRangeTypes = new ArrayList<>(defaultTypes);
        issueRangeTypes.add(PREVIOUS_AND_THIS_MONTH);
        issueRangeTypes = Collections.unmodifiableList(issueRangeTypes);

        reportRangeTypes = Collections.unmodifiableList(defaultTypes);

        dutyTypes = unmodifiableListOf(
                FIXED,
                TODAY,
                THIS_WEEK,
                THIS_WEEK_AND_BEYOND,
                NEXT_WEEK,
                LAST_WEEK,
                THIS_MONTH,
                LAST_MONTH,
                NEXT_MONTH,
                THIS_YEAR,
                LAST_YEAR
        );
    }

    public static List<En_DateIntervalType> defaultTypes() { return defaultTypes; }

    public static List<En_DateIntervalType> reservedIpTypes() { return reservedIpRangeTypes; }

    public static List<En_DateIntervalType> issueTypes() { return issueRangeTypes; }

    public static List<En_DateIntervalType> reportTypes() { return reportRangeTypes; }

    public static List<En_DateIntervalType> dutyTypes() { return dutyTypes; }
}