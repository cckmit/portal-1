package ru.protei.portal.ui.absence.client.util;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.dto.Time;
import ru.protei.portal.core.model.dto.TimeInterval;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;
import java.util.stream.Collectors;

public class ScheduleFormatter {
    public static String getSchedule(List<ScheduleItem> values) {
        StringBuilder valueBuilder = new StringBuilder();
        values.forEach(value -> {
            valueBuilder.append(lang.absenceScheduleItemDescription(getDays(value), getTimeRanges(value)));
        });
        return valueBuilder.toString();
    }

    public static String getDays(ScheduleItem value) {
        return CollectionUtils.emptyIfNull(value.getDaysOfWeek())
                .stream()
                .map(day -> weekdays[day])
                .collect(Collectors.joining(", "));
    }

    public static String getTimeRanges(ScheduleItem value) {
        return CollectionUtils.emptyIfNull(value.getTimes())
                .stream()
                .map(ScheduleFormatter::formatTimePeriod)
                .collect(Collectors.joining(", "));
    }

    private static String formatTimePeriod(TimeInterval interval) {
        if (interval == null || interval.isEmpty()) {
            return "";
        }

        return lang.absenceTimeRange(formatTime(interval.getFrom()), formatTime(interval.getTo()));
    }

    private static String formatTime(Time value) {
        if (value == null) return "";
        return value.getHour() + ":" + value.getMinute();
    }

    @Inject
    static Lang lang;

    private static final String[] weekdays = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().weekdaysShortStandalone();
}
