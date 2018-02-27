package ru.protei.portal.redmine.utils;

import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Person;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RedmineUtils {
    public static String parseDateToAfter(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -3);
        return AFTER + dateTimeFormatter.format(calendar.getTime()) + "Z";
    }

    public static String parseDateToBefore(Date date) {
        return BEFORE + dateTimeFormatter.format(date);
    }

    public static String parseDateToRange(Date start, Date end) {
        return RANGE + dateTimeFormatter.format(start) + RANGE_SEPARATOR + dateTimeFormatter.format(end);
    }

    private static final String AFTER = ">=";
    private static final String BEFORE = "<=";
    private static final String RANGE = "><";
    private static final String RANGE_SEPARATOR = "|";


    private static final Format dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final Format dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
}
