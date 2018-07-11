package ru.protei.portal.ui.common.client.common;

import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

public class DateUtils {
    public static int getYearFromDate(Date date) {
        return Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(date));
    }
}
