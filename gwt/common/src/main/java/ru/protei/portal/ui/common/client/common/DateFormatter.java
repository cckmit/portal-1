package ru.protei.portal.ui.common.client.common;

import com.google.gwt.i18n.client.DateTimeFormat;

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

    private static DateTimeFormat format = DateTimeFormat.getFormat( "dd.MM.yyyy" );
    private static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat( "dd.MM.yyyy hh:mm" );
}
