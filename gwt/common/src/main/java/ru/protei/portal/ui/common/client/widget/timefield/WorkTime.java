package ru.protei.portal.ui.common.client.widget.timefield;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import ru.protei.portal.ui.common.client.lang.Lang;

public class WorkTime {

    public WorkTime(Lang lang) {
        day = lang.timeDayLiteral();
        hour = lang.timeHourLiteral();
        minute = lang.timeMinuteLiteral();
        placeholder = "1" + day + " 1" + hour + " 1" + minute;
        pattern = "^(\\d+" + RegExp.quote(day) + "\\s*)?(\\d+" + RegExp.quote(hour) + "\\s*)?(\\d+" + RegExp.quote(minute) + "\\s*)?" + RegExp.quote(unknown) + "?$"; // any order (didn't work):  "^(?:(\\d+" + day + "\\s*)?|(\\d+" + hour + "\\s*)?|(\\d+" + minute + "\\s*)?){1,3}$"
        regexp = RegExp.compile(pattern, "i");
    }

    public String asString(Long minutes) {
        if (minutes == null) {
            return unknown;
        }

        Long days = minutes / MINUTE2DAY;
        minutes = minutes % MINUTE2DAY;
        Long hours = minutes / MINUTE2HOUR;
        minutes = minutes % MINUTE2HOUR;

        String value = "";
        if (days > 0) {
            value += String.valueOf(days) + day + " ";
        }
        if (hours > 0) {
            value += String.valueOf(hours) + hour + " ";
        }
        if (minutes > 0) {
            value += String.valueOf(minutes) + minute + " ";
        }

        return value.trim();
    }

    public Long asTime(String value) {
        if (regexp == null || value==null || value.isEmpty() || value.equals(unknown)) {
            return null;
        }

        Long minutes = 0L;

        MatchResult match = regexp.exec(value);
        int groups = match.getGroupCount();
        if (groups > 0) {            minutes += parseGroup(match.getGroup(1)) * MINUTE2DAY;
        }
        if (groups > 1) {
            minutes += parseGroup(match.getGroup(2)) * MINUTE2HOUR;
        }
        if (groups > 2) {
            minutes += parseGroup(match.getGroup(3));
        }

        return minutes;
    }


    public String getPattern() {
        return pattern;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    private Long parseGroup(String group) {
        if (group == null || group.isEmpty()) {
            return 0L;
        }
        group = group.trim();
        group = group.substring(0, group.length() - 1);
        return Long.parseLong(group);
    }

    private String unknown = "?";
    private String day = "";
    private String hour = "";
    private String minute = "";
    private String placeholder = "";
    private String pattern = "\\S+";
    private RegExp regexp = null;
    private final static Long MINUTE2HOUR = 60L;
    private final static Long MINUTE2DAY = 8L * MINUTE2HOUR; // 8 hours per day



}
