package ru.protei.portal.ui.common.client.common;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.shared.TimeZone;

import java.util.Date;

/**
 * Created by shagaleev on 11/10/16.
 */
public class DateFormatter {
    public static String formatDateOnly( Date date ) {
        if ( date == null ) {
            return "";
        }

        return format.format( date );
    }

    public static String formatDateTime( Date date ) {
        if ( date == null ) {
            return "";
        }

        return dateTimeFormat.format( date );
    }

    public static String formatDateMonth( Date date ) {
        if ( date == null ) {
            return "";
        }

        return formatDateMonth( date, null );
    }

    public static String formatDateMonth( Date date, TimeZone timeZone ) {
        if ( date == null ) {
            return "";
        }

        return dateMonthFormat.format( date, timeZone );
    }
    public static String formatTimeOnly( Date date ) {
        if ( date == null ) {
            return "";
        }

        return timeFormat.format( date );
    }

    public static String formatYear(Date date) {
        return date == null ? "" : yearFormat.format(date);
    }


    private static DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");
    private static DateTimeFormat format = DateTimeFormat.getFormat( "dd.MM.yyyy" );
    private static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat( "dd.MM.yyyy HH:mm" );
    private static DateTimeFormat dateMonthFormat = DateTimeFormat.getFormat( "dd MMMM" );
    private static DateTimeFormat timeFormat = DateTimeFormat.getFormat( "HH:mm" );
}
