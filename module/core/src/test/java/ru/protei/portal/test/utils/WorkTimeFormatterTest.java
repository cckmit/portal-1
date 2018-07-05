package ru.protei.portal.test.utils;

import org.junit.Test;
import ru.protei.portal.core.utils.WorkTimeFormatter;

import static org.junit.Assert.*;

public class WorkTimeFormatterTest {

    long MINUTE = 1L;
    long HOUR = 60 * MINUTE;
    long WORKDAY = 8 * HOUR;

    long TESTTIME = 2 * WORKDAY + 3 * HOUR + 11 * MINUTE;

    @Test
    public void getMinutesTest() throws Exception {
        assertEquals("Expected minutes: ", new Long(11), WorkTimeFormatter.getMinutes(TESTTIME));
    }

    @Test
    public void getHoursTest() throws Exception {
        assertEquals("Expected hours: ", new Long(3), WorkTimeFormatter.getHours(TESTTIME));
    }

    @Test
    public void getDaysTest() throws Exception {
        assertEquals("Expected minutes: ", new Long(2), WorkTimeFormatter.getDays(TESTTIME));
    }

    @Test
    public void formatTest() throws Exception {
        WorkTimeFormatter formatter = new WorkTimeFormatter();
        assertEquals("Expected formatted string: ", "2д 3ч 11м", formatter.format(TESTTIME, "д", "ч", "м"));
    }

}
