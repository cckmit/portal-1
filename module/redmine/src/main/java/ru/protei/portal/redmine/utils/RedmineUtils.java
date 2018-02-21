package ru.protei.portal.redmine.utils;

import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Person;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RedmineUtils {
    public static String parseDateToAfter(Date date) {
        return AFTER + dateTimeFormatter.format(date) + "Z";
    }

    public static String parseDateToBefore(Date date) {
        return BEFORE + dateTimeFormatter.format(date);
    }

    public static String parseDateToRange(Date start, Date end) {
        return RANGE + dateTimeFormatter.format(start) + RANGE_SEPARATOR + dateTimeFormatter.format(end);
    }

    public static CaseComment parseJournal(Journal journal) {
        CaseComment comment = new CaseComment();
        comment.setCreated(journal.getCreatedOn());
        Person author = parseUser(journal.getUser());
        comment.setAuthor(author);
        comment.setId(Long.valueOf(journal.getId()));
        comment.setText(journal.getNotes());
        return comment;
    }

    public static Person parseUser(User user) {
        Person person = new Person();
        person.setFirstName(user.getFirstName());
        person.setId(Long.valueOf(user.getId()));
        person.setLastName(user.getLastName());
        person.setCreated(user.getCreatedOn());
        person.setDisplayName(user.getFirstName());
        user.getStatus();
        return person;
    }


    private static final String AFTER = ">=";
    private static final String BEFORE = "<=";
    private static final String RANGE = "><";
    private static final String RANGE_SEPARATOR = "|";


    private static final Format dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final Format dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
}
