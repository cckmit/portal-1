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
    LAST_WEEK,
    THIS_MONTH,
    LAST_MONTH,
    THIS_YEAR,
    LAST_YEAR;

    private static List<En_DateIntervalType> allTypes;
    private static List<En_DateIntervalType> reservedIpRangeTypes;
    private static List<En_DateIntervalType> issueRangeTypes;
    private static List<En_DateIntervalType> reportRangeTypes;

    static {
        allTypes = new ArrayList<>(9);
        allTypes.add(FIXED);
        allTypes.add(TODAY);
        allTypes.add(YESTERDAY);
        allTypes.add(THIS_WEEK);
        allTypes.add(LAST_WEEK);
        allTypes.add(THIS_MONTH);
        allTypes.add(LAST_MONTH);
        allTypes.add(THIS_YEAR);
        allTypes.add(LAST_YEAR);

        reservedIpRangeTypes = new ArrayList<>(3);
        reservedIpRangeTypes.add(MONTH);
        reservedIpRangeTypes.add(FIXED);
        reservedIpRangeTypes.add(UNLIMITED);

        issueRangeTypes = new ArrayList<>(allTypes);

        reportRangeTypes = new ArrayList<>(issueRangeTypes);
    }

    public static List<En_DateIntervalType> allTypes() { return allTypes; }

    public static List<En_DateIntervalType> reservedIpTypes() { return reservedIpRangeTypes; }

    public static List<En_DateIntervalType> issueTypes() { return issueRangeTypes; }

    public static List<En_DateIntervalType> reportTypes() { return reportRangeTypes; }
}