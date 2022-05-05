package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.dto.Time;
import ru.protei.portal.core.model.dto.TimeInterval;
import ru.protei.portal.core.model.ent.PersonAbsence;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AbsenceUtils {

    public static List<PersonAbsence> convertToDateAbsence(PersonAbsence scheduleAbsence, Date from, Date to) {
        if (from == null || to == null || scheduleAbsence == null || CollectionUtils.isEmpty(scheduleAbsence.getScheduleItems()))
            return Collections.emptyList();
        LocalDateTime startDate = convertToLocalDateViaInstant(from.before(scheduleAbsence.getFromTime()) ? scheduleAbsence.getFromTime() : from);
        LocalDateTime endDate = convertToLocalDateViaInstant(to.after(scheduleAbsence.getTillTime()) ? scheduleAbsence.getTillTime() : to);

        Map<Integer, List<TimeInterval>> dayOfWeekToTime = getDayOfWeekToTimeMap( scheduleAbsence.getScheduleItems() );

        List<LocalDateTime> datesBetween = Stream.iterate(startDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .filter(date -> dayOfWeekToTime.containsKey(date.getDayOfWeek().getValue()))
                .collect(Collectors.toList());

        List<PersonAbsence> absences = new ArrayList<>();
        for (LocalDateTime currentDate : datesBetween) {
            List<TimeInterval> intervals = dayOfWeekToTime.get(currentDate.getDayOfWeek().getValue());
            if (CollectionUtils.isEmpty(intervals)) {
                break;
            }

            for (TimeInterval interval : intervals) {
                TimeInterval current = interval;
                // check start interval time
                if (currentDate.toLocalDate().equals(startDate.toLocalDate())){
                    Time beginTime = new Time(startDate.getHour(), startDate.getMinute());
                    if (current.getTo().before(beginTime)) {
                        break;
                    }
                    if (current.getFrom().before(beginTime)) {
                        current = new TimeInterval(beginTime, current.getTo());
                    }
                }

                // check end interval time
                if (currentDate.toLocalDate().equals(endDate.toLocalDate())) {
                    Time endTime = new Time(endDate.getHour(), endDate.getMinute());
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

    private static PersonAbsence generatePersonAbsenceFromInterval(PersonAbsence scheduleAbsence, LocalDateTime localDate, TimeInterval interval) {
        Date from = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
        from.setHours(interval.getFrom().getHour());
        from.setMinutes(interval.getFrom().getMinute());
        if (from.before(scheduleAbsence.getFromTime())) {
            from = scheduleAbsence.getTillTime();
        }

        Date to = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
        to.setHours(interval.getTo().getHour());
        to.setMinutes(interval.getTo().getMinute());
        if (to.after(scheduleAbsence.getTillTime())) {
            to = scheduleAbsence.getTillTime();
        }

        return new PersonAbsence(null, scheduleAbsence.getPersonId(), scheduleAbsence.getReason(), from, to);
    }

    private static LocalDateTime convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
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
