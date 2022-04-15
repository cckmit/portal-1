package ru.protei.portal.core.model.util;


import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.dto.TimeInterval;
import ru.protei.portal.core.model.helper.CollectionUtils;

import java.util.*;

public class ScheduleValidator {

    public static En_ResultStatus isValidScheduleItem(ScheduleItem value) {
        if (value == null) return En_ResultStatus.OK;
        if (CollectionUtils.isEmpty(value.getTimes())) return En_ResultStatus.SCHEDULE_NEED_FEEL_TIME_RANGES;
        if (CollectionUtils.isEmpty(value.getDaysOfWeek())) return En_ResultStatus.SCHEDULE_NEED_FEEL_DAYS;
        return isValidIntervals(value.getTimes());
    }

    public static En_ResultStatus isValidSchedule(List<ScheduleItem> value) {
        if (CollectionUtils.isEmpty(value)) return En_ResultStatus.OK;
        Map<Integer, List<TimeInterval>> dayToIntervalMap = new HashMap<>();
        for (ScheduleItem scheduleItem : value) {
            En_ResultStatus itemValidationStatus = isValidScheduleItem(scheduleItem);
            if (itemValidationStatus != En_ResultStatus.OK) return itemValidationStatus;
            scheduleItem.getDaysOfWeek().forEach(day ->
                    dayToIntervalMap.computeIfAbsent(day, k -> new ArrayList<>()).addAll(scheduleItem.getTimes()));
        }

        for (List<TimeInterval> intervals : dayToIntervalMap.values()) {
            En_ResultStatus intervalValidationStatus = isValidIntervals(intervals);
            if (intervalValidationStatus != En_ResultStatus.OK) return intervalValidationStatus;
        }

        return En_ResultStatus.OK;
    }

    public static En_ResultStatus isValidIntervals(List<TimeInterval> intervals) {
        intervals.sort(Comparator.nullsFirst(Comparator.comparing(TimeInterval::getFrom)));

        for (int i = 0; i < intervals.size(); i++) {
            if(!intervals.get(i).isValid()) {
                return En_ResultStatus.SCHEDULE_INCORRECT_TIME_RANGE;
            }
            if (i == intervals.size() - 1) continue;
            if (hasOverlap(intervals.get(i), intervals.get(i + 1))) {
                return En_ResultStatus.SCHEDULE_HAS_OVERLAP;
            }
        }

        return En_ResultStatus.OK;
    }

    private static boolean hasOverlap(TimeInterval t1, TimeInterval t2) {
        return !t1.getTo().before(t2.getFrom()) && !t1.getFrom().after(t2.getTo());
    }
}
