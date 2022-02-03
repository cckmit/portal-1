package ru.protei.portal.core.utils;

import static java.time.Duration.ofMinutes;
import static java.util.Optional.ofNullable;
import static org.apache.poi.ss.usermodel.DateUtil.SECONDS_PER_DAY;

public class ExcelFormatUtils {
    public interface ExcelFormat {
        String STANDARD = "@";
        String DATE_TIME = "DD.MM.YY HH:MM";
        String INFINITE_HOURS_MINUTES = "[H]:MM";
        String FULL_DATE = "DD.MM.YYYY";
        String FULL_DATE_TIME = "DD.MM.YYYY HH:MM";
        String NUMBER = "0";
        String REVERSED_DASHED_FULL_DATE = "YYYY-MM-DD";
        
        int DEFAULT_WIDTH = 5800;
    }

    public static double toExcelTimeFormat(Long minutes) {
        return (double) ofMinutes(ofNullable(minutes).orElse(0L)).getSeconds() / SECONDS_PER_DAY;
    }

    public static String toDaysHoursMinutes(long min) {
        if (min == 0) {
            return "0, 00:00";
        }

        long days = WorkTimeFormatter.getFullDayTimeDays(min);
        String hours = String.format("%02d", WorkTimeFormatter.getFullDayTimeHours(min));       // with leading zero
        String minutes = String.format("%02d", WorkTimeFormatter.getFullDayTimeMinutes(min));

        return days + ", " + hours + ":" + minutes;
    }
}
