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
    }

    public static double toExcelTimeFormat(Long minutes) {
        return (double) ofMinutes(ofNullable(minutes).orElse(0L)).getSeconds() / SECONDS_PER_DAY;
    }
}
