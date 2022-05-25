package ru.protei.portal.ui.common.client.common;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.shared.TimeZone;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.Date;

/**
 * Created by shagaleev on 11/10/16.
 */
public class DateFormatter {
    public static String formatDateOnly( Date date ) {
        if ( date == null ) {
            return StringUtils.EMPTY;
        }

        return format.format( date );
    }

    public static String formatDateTime( Date date ) {
        if ( date == null ) {
            return StringUtils.EMPTY;
        }

        return dateTimeFormat.format( date );
    }

    public static String formatDateMonth( Date date ) {
        if ( date == null ) {
            return StringUtils.EMPTY;
        }

        return formatDateMonth( date, null );
    }

    public static String formatDateMonth( Date date, TimeZone timeZone ) {
        if ( date == null ) {
            return StringUtils.EMPTY;
        }

        return dateMonthFormat.format( date, timeZone );
    }
    public static String formatTimeOnly( Date date ) {
        if ( date == null ) {
            return StringUtils.EMPTY;
        }

        return timeFormat.format( date );
    }

    public static String formatYear(Date date) {
        return date == null ? StringUtils.EMPTY : yearFormat.format(date);
    }


    public static String formatYearMonthFullDay(Date date) {
        return date == null ? StringUtils.EMPTY : formatYearMonthFullDay.format(date);
    }

    public static String formatMonthFullDay(Date date) {
        return date == null ? StringUtils.EMPTY : formatMonthFullDay.format(date);
    }
    private static DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");
    private static DateTimeFormat format = DateTimeFormat.getFormat( "dd.MM.yyyy" );
    private static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat( "dd.MM.yyyy HH:mm" );
    private static DateTimeFormat dateMonthFormat = DateTimeFormat.getFormat( "dd MMMM" );
    private static DateTimeFormat timeFormat = DateTimeFormat.getFormat( "HH:mm" );
    private static DateTimeFormat formatYearMonthFullDay = DateTimeFormat.getFormat(LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().formatYearMonthFullDay());
    private static DateTimeFormat formatMonthFullDay = DateTimeFormat.getFormat(LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().formatMonthFullDay());

}
