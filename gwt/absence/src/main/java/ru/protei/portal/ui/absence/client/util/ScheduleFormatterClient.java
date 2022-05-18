package ru.protei.portal.ui.absence.client.util;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.dto.Time;
import ru.protei.portal.core.model.dto.TimeInterval;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;
import java.util.stream.Collectors;

public class ScheduleFormatterClient {
    public static String getSchedule(List<ScheduleItem> values) {
        return values.stream()
                .map(value -> lang.absenceScheduleItemDescription(getDays(value), getTimeRanges(value)))
                .collect(Collectors.joining(", "));
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
                .map(ScheduleFormatterClient::formatTimePeriod)
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
        return timeFormat.format(value.getHour()) + ":" + timeFormat.format(value.getMinute());
    }

    @Inject
    static Lang lang;

    private static final NumberFormat timeFormat = NumberFormat.getFormat("00");
    private static final String[] weekdays = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().weekdaysShortStandalone();
}
