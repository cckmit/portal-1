package ru.protei.portal.test.utils;

import org.junit.Test;
import ru.protei.portal.core.utils.WorkTimeFormatter;

import static org.junit.Assert.assertEquals;
import static ru.protei.portal.core.utils.WorkTimeFormatter.*;

public class WorkTimeFormatterTest {

    String dayLiteral = "д";
    String hourLiteral = "ч";
    String minuteLiteral = "м";

    Long workDays = 2L;
    Long workHours = 3L;
    Long workMinutes = 11L;
    long TESTTIME = workDays * DAY + workHours * HOUR + workMinutes * MINUTE;

    @Test
    public void getMinutesTest() throws Exception {
        assertEquals("Expected minutes: ", workMinutes, WorkTimeFormatter.getMinutes(TESTTIME));
    }

    @Test
    public void getHoursTest() throws Exception {
        assertEquals("Expected hours: ", workHours, WorkTimeFormatter.getHours(TESTTIME));
    }

    @Test
    public void getDaysTest() throws Exception {
        assertEquals("Expected days: ", workDays, WorkTimeFormatter.getDays(TESTTIME));
    }

    @Test
    public void formatTest() throws Exception {
        WorkTimeFormatter formatter = new WorkTimeFormatter();

        String expectedString = String.join("", workDays.toString(), dayLiteral, " ", workHours.toString(), hourLiteral, " ", workMinutes.toString(), minuteLiteral);
        assertEquals("Expected formatted string: ", expectedString, formatter.format(TESTTIME, dayLiteral, hourLiteral, minuteLiteral));
    }

}
