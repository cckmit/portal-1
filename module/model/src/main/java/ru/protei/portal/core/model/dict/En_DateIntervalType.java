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

    public static List<En_DateIntervalType> reservedIpTypes() {
        List<En_DateIntervalType> list = new ArrayList<>();
        list.add(MONTH);
        list.add(FIXED);
        list.add(UNLIMITED);
        return list;
    }

    public static List<En_DateIntervalType> issueTypes() {
        List<En_DateIntervalType> list = new ArrayList<>();
        list.add(FIXED);
        list.add(TODAY);
        list.add(YESTERDAY);
        list.add(THIS_WEEK);
        list.add(LAST_WEEK);
        list.add(THIS_MONTH);
        list.add(LAST_MONTH);
        list.add(THIS_YEAR);
        list.add(LAST_YEAR);
        return list;
    }
}
