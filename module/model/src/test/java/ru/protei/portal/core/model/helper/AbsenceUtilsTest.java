package ru.protei.portal.core.model.helper;

import org.junit.Test;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.dto.Time;
import ru.protei.portal.core.model.dto.TimeInterval;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class AbsenceUtilsTest {

    private static final int YEAR = 2022;
    private static final int MONTH = Calendar.JANUARY;

    @Test
    public void testConvertScheduleToDateAbsenceInRange() {
        TimeInterval intervalFirst = new TimeInterval(new Time(11, 10), new Time(13, 40));
        TimeInterval intervalSecond = new TimeInterval(new Time(19, 15), new Time(20, 0));
        List<TimeInterval> intervals = Arrays.asList(intervalFirst, intervalSecond);

        Date scheduleFrom = new Date(YEAR, MONTH, 1, 0, 0, 0);
        Date scheduleTo = new Date(YEAR, MONTH, 10, 23, 59, 59);

        PersonAbsence scheduleAbsence = generateAbsenceWithSchedule(intervals, Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY), scheduleFrom, scheduleTo);
        Date from = new Date(YEAR, MONTH, 5, 0, 0, 0);
        Date to = new Date(YEAR, MONTH, 15, 15, 0, 0);
        List<PersonAbsence> resultAbsences = AbsenceUtils.convertToDateAbsence(scheduleAbsence, from, to);
        assertEquals("Check count result is equals", resultAbsences.size(), 6);

        List<PersonAbsence> waitAbsences = new ArrayList<>();
        for (TimeInterval interval : intervals) {
            waitAbsences.add(generateDateAbsence(5, interval));
            waitAbsences.add(generateDateAbsence(7, interval));
            waitAbsences.add(generateDateAbsence(10, interval));
        }
        assertEquals("Check value result is equals", waitAbsences, resultAbsences);
    }

    @Test
    public void testConvertScheduleToDateAbsenceIntersectionRight() {
        TimeInterval intervalFirst = new TimeInterval(new Time(11, 10), new Time(13, 40));
        TimeInterval intervalSecond = new TimeInterval(new Time(19, 15), new Time(20, 0));
        List<TimeInterval> intervals = Arrays.asList(intervalFirst, intervalSecond);

        Date scheduleFrom = new Date(YEAR, MONTH, 1, 0, 0, 0);
        Date scheduleTo = new Date(YEAR, MONTH, 10, 23, 59, 59);

        PersonAbsence scheduleAbsence = generateAbsenceWithSchedule(intervals, Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY), scheduleFrom, scheduleTo);

        Date from = new Date(YEAR, MONTH, 5, 0, 0, 0);
        Date to = new Date(YEAR, MONTH, 7, 13, 0, 0);
        List<PersonAbsence> resultAbsences = AbsenceUtils.convertToDateAbsence(scheduleAbsence, from, to);
        assertEquals("Check count result is equals", resultAbsences.size(), 3);

        List<PersonAbsence> waitAbsences = new ArrayList<>();
        for (TimeInterval interval : intervals) {
            waitAbsences.add(generateDateAbsence(5, interval));
        }
        waitAbsences.add(generateDateAbsence(7, new TimeInterval(new Time(11, 10), new Time(13, 0))));
        assertEquals("Check value result is equals", waitAbsences, resultAbsences);
    }

    @Test
    public void testConvertScheduleToDateAbsenceWithoutIntersection() {
        TimeInterval intervalFirst = new TimeInterval(new Time(11, 10), new Time(13, 40));
        TimeInterval intervalSecond = new TimeInterval(new Time(19, 15), new Time(20, 0));
        List<TimeInterval> intervals = Arrays.asList(intervalFirst, intervalSecond);

        Date scheduleFrom = new Date(YEAR, MONTH, 1, 0, 0, 0);
        Date scheduleTo = new Date(YEAR, MONTH, 10, 23, 59, 59);

        PersonAbsence scheduleAbsence = generateAbsenceWithSchedule(intervals, Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY), scheduleFrom, scheduleTo);

        Date from = new Date(YEAR, MONTH, 5, 0, 0, 0);
        Date to = new Date(YEAR, MONTH, 5, 10, 0, 0);
        List<PersonAbsence> resultAbsences = AbsenceUtils.convertToDateAbsence(scheduleAbsence, from, to);
        assertEquals("Check count result is equals", resultAbsences.size(), 0);
    }

    @Test
    public void testConvertScheduleToDateAbsenceNotInSchedule() {
        // 4
        TimeInterval intervalFirst = new TimeInterval(new Time(11, 10), new Time(13, 40));
        TimeInterval intervalSecond = new TimeInterval(new Time(19, 15), new Time(20, 0));
        List<TimeInterval> intervals = Arrays.asList(intervalFirst, intervalSecond);

        Date scheduleFrom = new Date(YEAR, MONTH, 1, 0, 0, 0);
        Date scheduleTo = new Date(YEAR, MONTH, 10, 23, 59, 59);

        PersonAbsence scheduleAbsence = generateAbsenceWithSchedule(intervals, Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY), scheduleFrom, scheduleTo);

        Date from = new Date(YEAR, MONTH, 6, 0, 0, 0);
        Date to = new Date(YEAR, MONTH, 6, 23, 59, 0);
        List<PersonAbsence> resultAbsences = AbsenceUtils.convertToDateAbsence(scheduleAbsence, from, to);
        assertEquals("Check count result is equals", resultAbsences.size(), 0);
    }

    @Test
    public void testConvertScheduleToDateAbsenceIntersectionLeft() {
        TimeInterval intervalFirst = new TimeInterval(new Time(11, 10), new Time(13, 40));
        TimeInterval intervalSecond = new TimeInterval(new Time(19, 15), new Time(20, 0));
        List<TimeInterval> intervals = Arrays.asList(intervalFirst, intervalSecond);

        Date scheduleFrom = new Date(YEAR, MONTH, 1, 0, 0, 0);
        Date scheduleTo = new Date(YEAR, MONTH, 10, 23, 59, 59);

        PersonAbsence scheduleAbsence = generateAbsenceWithSchedule(intervals, Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY), scheduleFrom, scheduleTo);

        Date from = new Date(YEAR, MONTH, 5, 12, 0, 0);
        Date to = new Date(YEAR, MONTH, 6, 23, 59, 0);
        List<PersonAbsence> resultAbsences = AbsenceUtils.convertToDateAbsence(scheduleAbsence, from, to);
        assertEquals("Check count result is equals", resultAbsences.size(), 2);

        List<PersonAbsence> waitAbsences = new ArrayList<>();
        waitAbsences.add(generateDateAbsence(5, new TimeInterval(new Time(12, 0), new Time(13, 0))));
        waitAbsences.add(generateDateAbsence(5, intervalSecond));

        assertEquals("Check value result is equals", waitAbsences, resultAbsences);
    }

    private PersonAbsence generateAbsenceWithSchedule(List<TimeInterval> intervals, List<Integer> days, Date from, Date to) {
        ScheduleItem scheduleItem = new ScheduleItem();
        scheduleItem.setDaysOfWeek(days);
        scheduleItem.setTimes(intervals);

        PersonAbsence absence = new PersonAbsence(null, null, null, from, to);

        absence.setScheduleItems(Collections.singletonList(scheduleItem));
        return absence;
    }

    private PersonAbsence generateDateAbsence(int date, TimeInterval interval) {
        Date from = new Date(YEAR, MONTH, date, interval.getFrom().getHour(), interval.getFrom().getMinute(), 0);
        Date to = new Date(YEAR, MONTH, date, interval.getTo().getHour(), interval.getTo().getMinute(), 0);
        return new PersonAbsence(null, null, null, from, to);
    }
}