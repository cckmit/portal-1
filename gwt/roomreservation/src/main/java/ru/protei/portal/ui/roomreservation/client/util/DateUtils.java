package ru.protei.portal.ui.roomreservation.client.util;

import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.roomreservation.client.struct.YearMonthDay;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DateUtils {

    /**
     * Возвращает доступный для работы диапазон годов
     */
    public static List<Integer> makeAvailableYears() {
        int now = getYearNormalized(new Date());
        int from = now - 10;
        int until = now + 10;
        return IntStream.range(from, until + 1)
            .boxed()
            .collect(Collectors.toList());
    }

    /**
     * Возвращает доступный для работы диапазон месяцев
     */
    public static List<Integer> makeAvailableMonths() {
        int from = 1;
        int until = 12;
        return IntStream.range(from, until + 1)
            .boxed()
            .collect(Collectors.toList());
    }

    /**
     * Возвращает год из 4 цифр
     */
    public static int getYearNormalized(Date date) {
        return date.getYear() + 1900;
    }

    /**
     * Возвращает год, начиная с 1900 (год минус 1900)
     */
    public static int getYearDeNormalized(Integer year) {
        return year - 1900;
    }

    /**
     * Возвращает месяц в диапазоне 1-12
     */
    public static int getMonthNormalized(Date date) {
        return date.getMonth() + 1;
    }

    /**
     * Возвращает месяц в диапазоне 0-11
     */
    public static int getMonthDeNormalized(Integer month) {
        return month - 1;
    }

    /**
     * Возвращает день в месяце в диапазоне 1-31
     */
    public static int getDayOfMonth(Date date) {
        return date.getDate();
    }

    /**
     * Возвращает день в неделе в диапазоне 1-7 <br>
     * 1 - Понедельник, 7 - Воскресенье
     */
    public static int getDayOfWeekNormalized(Date date) {
        int day = date.getDay();
        if (day == 0) day = 7;
        return day;
    }

    /**
     * Возвращает день в неделе в диапазоне 0-6 <br>
     * 0 - Воскресенье, 6 - Суббота
     */
    public static int getDayOfWeekDeNormalized(Integer day) {
        if (day == 7) day = 0;
        return day;
    }

    /**
     * Возвращает количество дней в месяце
     */
    public static int getDaysInMonth(Integer month, Integer year) {
        if (month == null) {
            return 0;
        }
        switch (month) {
            case 2:
                return isLeapYear(year) ? 29 : 28;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            default:
                return 30;
        }
    }

    /**
     * Возвращает название месяца (диапазон 1-12)
     */
    public static String getMonthName(Integer month, Lang lang) {
        if (month == null) {
            return "?";
        }
        switch (month) {
            case  1: return lang.january();
            case  2: return lang.february();
            case  3: return lang.march();
            case  4: return lang.april();
            case  5: return lang.may();
            case  6: return lang.june();
            case  7: return lang.july();
            case  8: return lang.august();
            case  9: return lang.september();
            case 10: return lang.october();
            case 11: return lang.november();
            case 12: return lang.december();
        }
        return "?";
    }

    /**
     * Возвращает полное название дня в неделе (диапазон 1-7)
     */
    public static String getDayOfWeekName(Integer day, Lang lang) {
        if (day == null) {
            return "?";
        }
        switch (day) {
            case 1: return lang.monday();
            case 2: return lang.tuesday();
            case 3: return lang.wednesday();
            case 4: return lang.thursday();
            case 5: return lang.friday();
            case 6: return lang.saturday();
            case 7: return lang.sunday();
        }
        return "?";
    }

    /**
     * Возвращает короткое название дня в неделе (диапазон 1-7)
     */
    public static String getDayOfWeekNameShort(Integer day, Lang lang) {
        if (day == null) {
            return "?";
        }
        switch (day) {
            case 1: return lang.mondayShort();
            case 2: return lang.tuesdayShort();
            case 3: return lang.wednesdayShort();
            case 4: return lang.thursdayShort();
            case 5: return lang.fridayShort();
            case 6: return lang.saturdayShort();
            case 7: return lang.sundayShort();
        }
        return "?";
    }

    public static YearMonthDay makeYearMonthDay(Date date) {
        int year = getYearNormalized(date);
        int month = getMonthNormalized(date);
        int dayOfMonth = getDayOfMonth(date);
        return new YearMonthDay(year, month, dayOfMonth);
    }

    public static String leadingZero(int number, int digits) {
        int numberOfDigits = String.valueOf(number).length();
        int numberOfLeadingZeroes = digits - numberOfDigits;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfLeadingZeroes; i++) {
            sb.append("0");
        }
        sb.append(number);
        return sb.toString();
    }

    public static boolean isLeapYear(Integer year) {
        if (year == null) {
            return false;
        }
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
    }
}
