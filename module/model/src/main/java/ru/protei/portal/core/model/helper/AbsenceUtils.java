package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.dto.Time;
import ru.protei.portal.core.model.dto.TimeInterval;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.protei.portal.core.model.util.DateUtils.*;


public class AbsenceUtils {

    public static boolean checkScheduledAbsenceActiveTodayWithoutTimeCheck (PersonAbsence absence) {
        if (absence == null || !absence.isScheduledAbsence()) return false;

        Date now = new Date();
        if (compareOnlyDate(absence.getTillTime(), now) < 0) {
            return false;
        }

        return absence.getScheduleItems().stream()
                .anyMatch(item -> item.getDaysOfWeek().contains(now.getDay()));
    }

    public static List<PersonAbsence> convertToDateAbsence(PersonAbsence scheduleAbsence, Date from, Date to) {
        if (from == null || to == null || scheduleAbsence == null || CollectionUtils.isEmpty(scheduleAbsence.getScheduleItems()))
            return Collections.emptyList();

        Date startDate = from.before(scheduleAbsence.getFromTime()) ? scheduleAbsence.getFromTime() : from;
        Date endDate = to.after(scheduleAbsence.getTillTime()) ? scheduleAbsence.getTillTime() : to;

        Map<Integer, List<TimeInterval>> dayOfWeekToTime = getDayOfWeekToTimeMap( scheduleAbsence.getScheduleItems() );

        List<Date> datesBetween = Stream.iterate(startDate, d -> addDay(d))
                .limit(daysBetween(startDate, endDate) + 1)
                .filter(date -> dayOfWeekToTime.containsKey(date.getDay()))
                .collect(Collectors.toList());

        List<PersonAbsence> absences = new ArrayList<>();
        for (Date currentDate : datesBetween) {
            List<TimeInterval> intervals = dayOfWeekToTime.get(currentDate.getDay());
            if (CollectionUtils.isEmpty(intervals)) {
                break;
            }

            for (TimeInterval interval : intervals) {
                TimeInterval current = interval;
                // check start interval time
                if (isSameDay(currentDate, startDate)){
                    Time beginTime = new Time(startDate.getHours(), startDate.getMinutes());
                    if (current.getTo().before(beginTime)) {
                        break;
                    }
                    if (current.getFrom().before(beginTime)) {
                        current = new TimeInterval(beginTime, current.getTo());
                    }
                }

                // check end interval time
                if (isSameDay(currentDate,endDate)) {
                    Time endTime = new Time(endDate.getHours(), endDate.getMinutes());
                    if (current.getFrom().after(endTime)) {
                        break;
                    }
                    if (current.getTo().after(endTime)) {
                        current = new TimeInterval(current.getFrom(), endTime);
                    }
                }

                absences.add(generatePersonAbsenceFromInterval(scheduleAbsence, currentDate, current));
            }
        }

        return absences;
    }

    public static List<PersonAbsence> generateAbsencesFromDateRange(List<PersonAbsence> absences, Date from, Date to) {
        List<PersonAbsence> collectedAbsences = new ArrayList<>();
        for (PersonAbsence absence : absences) {
            if (!absence.isScheduledAbsence()) {
                collectedAbsences.add(absence);
            }

            List<PersonAbsence> scheduledAbsence = AbsenceUtils.convertToDateAbsence(absence, from, to);
            collectedAbsences.addAll(scheduledAbsence);
        }
        return collectedAbsences;
    }

    private static PersonAbsence generatePersonAbsenceFromInterval(PersonAbsence scheduleAbsence, Date date, TimeInterval interval) {
        Date from = new Date(date.getYear(), date.getMonth(), date.getDate(), interval.getFrom().getHour(), interval.getFrom().getMinute(), 0);
        if (from.before(scheduleAbsence.getFromTime())) {
            from = scheduleAbsence.getTillTime();
        }

        Date to = new Date(date.getYear(), date.getMonth(), date.getDate(), interval.getTo().getHour(), interval.getTo().getMinute(), 0);
        if (to.after(scheduleAbsence.getTillTime())) {
            to = scheduleAbsence.getTillTime();
        }

        return new PersonAbsence(null, scheduleAbsence.getPersonId(), scheduleAbsence.getReason(), from, to);
    }

    private static Map<Integer, List<TimeInterval>> getDayOfWeekToTimeMap(List<ScheduleItem> items) {
        Map<Integer, List<TimeInterval>> dayOfWeekToTime = new HashMap<>();
        for (ScheduleItem item : items) {
            for (Integer day : item.getDaysOfWeek()) {
                List<TimeInterval> intervals = dayOfWeekToTime.get(day);
                if (intervals != null) {
                    intervals.addAll(item.getTimes());
                } else {
                    dayOfWeekToTime.putIfAbsent(day, item.getTimes());
                }
            }
        }

        return dayOfWeekToTime;
    }

}
