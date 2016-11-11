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

    DateTimeFormat format = DateTimeFormat.getFormat( "yyyy-MM-dd" );
}
