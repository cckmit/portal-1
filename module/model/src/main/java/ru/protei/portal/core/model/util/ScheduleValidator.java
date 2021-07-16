package ru.protei.portal.core.model.util;


import ru.protei.portal.core.model.dict.ScheduleValidationStatus;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.Interval;

import java.util.*;

public class ScheduleValidator {

    public static ScheduleValidationStatus isValidScheduleItem(ScheduleItem value) {
        if (value == null) return ScheduleValidationStatus.NEED_FEEL_SCHEDULE;
        if (CollectionUtils.isEmpty(value.getTimes())) return ScheduleValidationStatus.NEED_FEEL_TIME_RANGES;
        if (CollectionUtils.isEmpty(value.getDaysOfWeek())) return ScheduleValidationStatus.NEED_FEEL_DAYS;
        return isValidIntervals(value.getTimes());
    }

    public static ScheduleValidationStatus isValidSchedule(List<ScheduleItem> value) {
        if (CollectionUtils.isEmpty(value)) return ScheduleValidationStatus.NEED_FEEL_SCHEDULE;
        Map<Integer, List<Interval>> dayToIntervalMap = new HashMap<>();
        for (ScheduleItem scheduleItem : value) {
            ScheduleValidationStatus itemValidationStatus = isValidScheduleItem(scheduleItem);
            if (itemValidationStatus != ScheduleValidationStatus.OK) return itemValidationStatus;
            scheduleItem.getDaysOfWeek().forEach(day ->
                    dayToIntervalMap.computeIfAbsent(day, k -> new ArrayList<>()).addAll(scheduleItem.getTimes()));
        }

        for (List<Interval> intervals : dayToIntervalMap.values()) {
            ScheduleValidationStatus intervalValidationStatus = isValidIntervals(intervals);
            if (intervalValidationStatus != ScheduleValidationStatus.OK) return intervalValidationStatus;
        }

        return ScheduleValidationStatus.OK;
    }

    public static ScheduleValidationStatus isValidIntervals(List<Interval> intervals) {
        intervals.sort(Comparator.nullsFirst(Comparator.comparing(date -> date.from)));

        for (int i = 0; i < intervals.size(); i++) {
            if(!intervals.get(i).isValid()) {
                return ScheduleValidationStatus.INCORRECT_TIME_RANGE;
            }
            if (i == intervals.size() - 1) continue;
            if (hasOverlap(intervals.get(i), intervals.get(i + 1))) {
                return ScheduleValidationStatus.HAS_OVERLAP;
            }
        }

        return ScheduleValidationStatus.OK;
    }

    private static boolean hasOverlap(Interval t1, Interval t2) {
        return !t1.to.before(t2.from) && !t1.from.after(t2.to);
    }
}
