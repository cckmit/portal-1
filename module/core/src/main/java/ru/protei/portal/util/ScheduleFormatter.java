package ru.protei.portal.util;


import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.dto.Time;
import ru.protei.portal.core.model.dto.TimeInterval;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.CrmConstants;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduleFormatter {
    private static final String TIME_FORMAT = "%02d";
    private final Lang lang;

    public ScheduleFormatter(Lang lang) {
        this.lang = lang;
    }

    public String getSchedule(List<ScheduleItem> values, String langCode) {
        if (values == null) return StringUtils.EMPTY;
        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(langCode));

        return values.stream()
                .map(value -> localizedLang.get("absenceScheduleItemDescription", new Object[]{getDays(value), getTimeRanges(localizedLang, value)}))
                .collect(Collectors.joining(", "));
    }

    private String getDays(ScheduleItem value) {
        Map<Integer, String> weekdayToNameMap = getWeekdayToNameMap();
        return CollectionUtils.emptyIfNull(value.getDaysOfWeek())
                .stream()
                .map(weekdayToNameMap::get)
                .collect(Collectors.joining(", "));
    }

    private String getTimeRanges(Lang.LocalizedLang localizedLang, ScheduleItem value) {
        return CollectionUtils.emptyIfNull(value.getTimes())
                .stream()
                .map(interval -> formatTimePeriod(localizedLang, interval))
                .collect(Collectors.joining(", "));
    }

    private String formatTimePeriod(Lang.LocalizedLang localizedLang, TimeInterval interval) {
        if (interval == null || interval.isEmpty()) {
            return "";
        }

        return localizedLang.get("absenceTimeRange", new Object[]{formatTime(interval.getFrom()), formatTime(interval.getTo())});
    }

    private String formatTime(Time value) {
        if (value == null) return "";
        return String.format(TIME_FORMAT, value.getHour()) + ":" + String.format(TIME_FORMAT,value.getMinute());
    }

    private Map<Integer, String> getWeekdayToNameMap() {
        Map<Integer, String> weekdayToNameMap = new HashMap<>();
        DayOfWeek[] dayOfWeeks = DayOfWeek.values();
        for (DayOfWeek dayOfWeek : dayOfWeeks) {
            weekdayToNameMap.put(dayOfWeek.getValue(),  dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag(CrmConstants.DEFAULT_LOCALE)));
        }
        return weekdayToNameMap;
    }
}
