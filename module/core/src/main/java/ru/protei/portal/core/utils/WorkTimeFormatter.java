package ru.protei.portal.core.utils;

public class WorkTimeFormatter {

    public WorkTimeFormatter () {
    }

    public WorkTimeFormatter(boolean isFullDayTime) {
        this.isFullDayTime = isFullDayTime;
    }

    public String format(Long minutes, String dayLiteral, String hourLiteral, String minuteLiteral) {
        if (minutes==null || minutes < 1) return "?";

        Long days = isFullDayTime ? getFullDayTimeDays(minutes) : getDays(minutes);
        Long hours = isFullDayTime ? getFullDayTimeHours(minutes) : getHours(minutes);
        Long minute = isFullDayTime ? getFullDayTimeMinutes(minutes) : getMinutes(minutes);

        StringBuilder sb = new StringBuilder();
        if (days != null && days > 0) {
            sb.append(days).append(dayLiteral);
        }
        if (hours != null && hours > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(hours).append(hourLiteral);
        }
        if (minute != null && minute > 0) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(minute).append(minuteLiteral);
        }

        return sb.toString();

    }

    /**
     * @return количество дней отбросив часы и минуты
     */
    public static Long getDays(Long minutes) {
        if (minutes == null) return null;
        return minutes / DAY;
    }

    /**
     * @return количество часов за вычетом дней отбросив минуты
     */
    public static Long getHours(Long minutes) {
        if (minutes == null) return null;
        return (minutes % DAY) / HOUR;
    }

    /**
     * @return количество минут за вычетом дней и часов
     */
    public static Long getMinutes(Long minutes) {
        if (minutes == null) return null;
        return (minutes % DAY) % HOUR;
    }

    public static Long getFullDayTimeDays(Long minutes) {
        if (minutes == null) return null;
        return minutes / FULLDAY;
    }

    public static Long getFullDayTimeHours(Long minutes) {
        if (minutes == null) return null;
        return (minutes % FULLDAY) / HOUR;
    }

    public static Long getFullDayTimeMinutes(Long minutes) {
        if (minutes == null) return null;
        return (minutes % FULLDAY) % HOUR;
    }

    public final static Long MINUTE = 1L;
    public final static Long HOUR = 60 * MINUTE;
    public final static Long DAY = 8 * HOUR;
    private final static Long FULLDAY = 24 * HOUR;
    private boolean isFullDayTime = false;
}
