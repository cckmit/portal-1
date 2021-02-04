package ru.protei.portal.core.model.dict;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

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
    LAST_YEAR,

    RELATIVE_LAST_DAY,
    RELATIVE_LAST_WEEK,
    RELATIVE_LAST_MONTH,
    RELATIVE_LAST_THREE_MONTHS,
    RELATIVE_LAST_HALF_YEAR,
    RELATIVE_LAST_YEAR
    ;

    private static final List<En_DateIntervalType> defaultTypes;
    private static final List<En_DateIntervalType> dutyTypes;
    private static final List<En_DateIntervalType> reservedIpRangeTypes;
    private static final List<En_DateIntervalType> issueRangeTypes;
    private static final List<En_DateIntervalType> reportRangeTypes;
    private static final List<En_DateIntervalType> reservedIpNonActiveTypes;

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

        ArrayList<En_DateIntervalType> tmpIssueRangeTypes = new ArrayList<>(defaultTypes);
        tmpIssueRangeTypes.add( PREVIOUS_AND_THIS_MONTH );
        tmpIssueRangeTypes.add( RELATIVE_LAST_DAY );
        tmpIssueRangeTypes.add( RELATIVE_LAST_WEEK );
        tmpIssueRangeTypes.add( RELATIVE_LAST_YEAR );
        issueRangeTypes = Collections.unmodifiableList(tmpIssueRangeTypes);

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

        reservedIpNonActiveTypes = unmodifiableListOf (
                RELATIVE_LAST_MONTH,
                RELATIVE_LAST_THREE_MONTHS,
                RELATIVE_LAST_HALF_YEAR,
                RELATIVE_LAST_YEAR
        );
    }

    public static List<En_DateIntervalType> defaultTypes() { return defaultTypes; }

    public static List<En_DateIntervalType> reservedIpTypes() { return reservedIpRangeTypes; }

    public static List<En_DateIntervalType> issueTypes() { return issueRangeTypes; }

    public static List<En_DateIntervalType> reportTypes() { return reportRangeTypes; }

    public static List<En_DateIntervalType> dutyTypes() { return dutyTypes; }

    public static List<En_DateIntervalType> reservedIpNonActiveTypes() { return reservedIpNonActiveTypes; }
}