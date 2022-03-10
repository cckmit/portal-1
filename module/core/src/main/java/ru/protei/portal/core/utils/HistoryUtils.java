package ru.protei.portal.core.utils;

import java.util.Date;

public class HistoryUtils {
    public static boolean dateAdded(Date oldDate, Date newDate) {
        return oldDate == null && newDate != null;
    }

    public static boolean dateChanged(Date oldDate, Date newDate) {
        return oldDate != null && newDate != null && oldDate.getTime() != newDate.getTime();
    }

    public static boolean dateRemoved(Date oldDate, Date newDate) {
        return oldDate != null && newDate == null;
    }
}
