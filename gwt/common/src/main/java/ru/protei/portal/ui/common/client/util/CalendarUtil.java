package ru.protei.portal.ui.common.client.util;

import java.util.Date;

public class CalendarUtil {
    public static boolean isSameDate(Date date0, Date date1) {
        if (date0 == null || date1 == null) {
            return date0 == null && date1 == null;
        }

        if (date0.getYear() != date1.getYear()) {
            return false;
        }

        if (date0.getMonth() != date1.getMonth()) {
            return false;
        }

        if (date0.getDate() != date1.getDate()) {
            return false;
        }

        return true;
    }

    public static boolean isPauseDateExpired(Date date) {
        if (date == null) {
            return true;
        }

        Date currentDate = new Date();

        if (isSameDate(currentDate, date)) {
            return false;
        }

        if (currentDate.compareTo(date) < 0) {
            return false;
        }

        return true;
    }
}
