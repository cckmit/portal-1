package ru.protei.portal.core.model.dict;

import java.util.ArrayList;
import java.util.List;

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
    NEXT_MONTH,
    THIS_YEAR,
    LAST_YEAR;

    private static List<En_DateIntervalType> defaultTypes;
    private static List<En_DateIntervalType> dutyTypes;
    private static List<En_DateIntervalType> reservedIpRangeTypes;
    private static List<En_DateIntervalType> issueRangeTypes;
    private static List<En_DateIntervalType> reportRangeTypes;

    static {
        defaultTypes = new ArrayList<>(9);
        defaultTypes.add(FIXED);
        defaultTypes.add(TODAY);
        defaultTypes.add(YESTERDAY);
        defaultTypes.add(THIS_WEEK);
        defaultTypes.add(LAST_WEEK);
        defaultTypes.add(THIS_MONTH);
        defaultTypes.add(LAST_MONTH);
        defaultTypes.add(THIS_YEAR);
        defaultTypes.add(LAST_YEAR);

        reservedIpRangeTypes = new ArrayList<>(3);
        reservedIpRangeTypes.add(MONTH);
        reservedIpRangeTypes.add(FIXED);
        reservedIpRangeTypes.add(UNLIMITED);

        issueRangeTypes = new ArrayList<>(defaultTypes);

        reportRangeTypes = new ArrayList<>(issueRangeTypes);

        dutyTypes = new ArrayList<>(11);
        dutyTypes.add(FIXED);
        dutyTypes.add(TODAY);
        dutyTypes.add(THIS_WEEK);
        dutyTypes.add(THIS_WEEK_AND_BEYOND);
        dutyTypes.add(NEXT_WEEK);
        dutyTypes.add(LAST_WEEK);
        dutyTypes.add(THIS_MONTH);
        dutyTypes.add(LAST_MONTH);
        dutyTypes.add(NEXT_MONTH);
        dutyTypes.add(THIS_YEAR);
        dutyTypes.add(LAST_YEAR);
    }

    public static List<En_DateIntervalType> defaultTypes() { return defaultTypes; }

    public static List<En_DateIntervalType> reservedIpTypes() { return reservedIpRangeTypes; }

    public static List<En_DateIntervalType> issueTypes() { return issueRangeTypes; }

    public static List<En_DateIntervalType> reportTypes() { return reportRangeTypes; }

    public static List<En_DateIntervalType> dutyTypes() { return dutyTypes; }
}