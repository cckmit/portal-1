package ru.protei.portal.core.utils;

public class TimeFormatter {

    public String formatHourMinutes(Long minutes) {

        if (minutes == null || minutes == 0) {
            return "0";
        }

        long hours = minutes / 60L;
        minutes -= hours * 60L;

        return ldgZero(hours) + ":" + ldgZero(minutes);
    }

    public String formatHourMinutesSeconds(Long seconds) {

        if (seconds == null || seconds == 0) {
            return "0";
        }

        long hours = seconds / (60L * 60L);
        seconds -= hours * 60L * 60L;
        long minutes = seconds / 60L;
        seconds -= minutes * 60L;

        return ldgZero(hours) + ":" + ldgZero(minutes) + ":" + ldgZero(seconds);
    }

    private String ldgZero(long unit) {
        if (unit >= 0 && unit < 10) {
            return "0" + unit;
        }
        return Long.toString(unit);
    }
}
