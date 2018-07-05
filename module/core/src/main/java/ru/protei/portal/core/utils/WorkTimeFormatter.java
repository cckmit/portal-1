package ru.protei.portal.core.utils;

public class WorkTimeFormatter {

    public String format(Long minutes, String dayLiteral, String hourLiteral, String minuteLiteral) {
        if (minutes==null || minutes < 1) return "?";

        Long days = getDays(minutes);
        Long hours = getHours(minutes);
        Long minute = getMinutes(minutes);

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

    public static Long getDays(Long minutes) {
        if (minutes == null) return null;
        return minutes / DAY;
    }

    public static Long getHours(Long minutes) {
        if (minutes == null) return null;
        return (minutes % DAY) / HOUR;
    }

    public static Long getMinutes(Long minutes) {
        if (minutes == null) return null;
        return (minutes % DAY) % HOUR;
    }

    private final static Long HOUR = 60L;
    private final static Long DAY = 8L * HOUR;

}
