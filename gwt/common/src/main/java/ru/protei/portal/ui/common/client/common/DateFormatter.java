package ru.protei.portal.ui.common.client.common;

import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * Created by shagaleev on 11/10/16.
 */
public class DateFormatter {
    public String formatDateOnly( Date date ) {
        if ( date == null ) {
            return "";
        }

        return format.format( date );
    }

    public String formatDateTime( Date date ) {
        if ( date == null ) {
            return "";
        }

        return dateTimeFormat.format( date );
    }

    DateTimeFormat format = DateTimeFormat.getFormat( "dd.MM.yyyy" );
    DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat( "dd.MM.yyyy hh:mm" );
}
