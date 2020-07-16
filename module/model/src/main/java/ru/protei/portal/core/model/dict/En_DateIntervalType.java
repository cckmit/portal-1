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

    private static List<En_DateIntervalType> reservedIpRangeTypes;
    private static List<En_DateIntervalType> issueRangeTypes;

    static {
        reservedIpRangeTypes = new ArrayList<>(3);
        reservedIpRangeTypes.add(MONTH);
        reservedIpRangeTypes.add(FIXED);
        reservedIpRangeTypes.add(UNLIMITED);

        issueRangeTypes = new ArrayList<>(9);
        issueRangeTypes.add(FIXED);
        issueRangeTypes.add(TODAY);
        issueRangeTypes.add(YESTERDAY);
        issueRangeTypes.add(THIS_WEEK);
        issueRangeTypes.add(LAST_WEEK);
        issueRangeTypes.add(THIS_MONTH);
        issueRangeTypes.add(LAST_MONTH);
        issueRangeTypes.add(THIS_YEAR);
        issueRangeTypes.add(LAST_YEAR);
    }

    public static List<En_DateIntervalType> reservedIpTypes() { return reservedIpRangeTypes; }

    public static List<En_DateIntervalType> issueTypes() { return issueRangeTypes; }
}
