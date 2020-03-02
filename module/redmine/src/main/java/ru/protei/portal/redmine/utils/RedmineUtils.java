package ru.protei.portal.redmine.utils;

import com.taskadapter.redmineapi.bean.User;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.RedmineEndpoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Supplier;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public final class RedmineUtils {
    //Somewhy datetime in issues stored in GMT timezone, therefore we need -3 hours from our time
    public static String parseDateToAfter(Date date) {
        /**
         * Вот это беда, Сергей.
         * Ты сохраняешь дату и время, которую получил от их системы,
         * она по идее, должна быть в UTC, но видимо библиотека возвращает нам дату в нашей временной зоне.
         *
         * просто вычитать 3 часа - это не корректно, нужно будет подумать над правильной реализацией.
         * Пока что, самое корректное, использовать форматтер с указанием тайм-зоны UTC
         */
//        calendar.add(Calendar.HOUR, -3);
        return AFTER + dateTimeFormatter.format(date) + "Z";
    }

    public static String userInfo (User user) {
        if (user == null)
            return "unknown";

        StringBuilder sb = new StringBuilder();
        sb.append("user[id=").append(user.getId()).append(",name=");
        if (user.getLastName() != null)
            sb.append(user.getLastName()).append(" ");
        else
            sb.append("? ");

        if (user.getFirstName() != null) {
            sb.append(user.getFirstName());
        }
        else
            sb.append("?");

        sb.append("]");
        return sb.toString();
    }

    public static Date maxDate(Date a, Date b) {
        return a == null ? b : b == null ? a : a.after(b) ? a : b;
    }

    public static class EndpointAndIssueId {
        public RedmineEndpoint endpoint;
        public Integer IssueId;

        public EndpointAndIssueId(RedmineEndpoint endpoint, Integer issueId) {
            this.endpoint = endpoint;
            this.IssueId = issueId;
        }
    }

    private static final String AFTER = ">=";
    private static final SimpleDateFormat dateTimeFormatter;

    static {
        dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        /** это нужно,
         * чтобы при форматировании даты и времени,
         * они не получали смещение текущей временной зоны сервера*
         */
        dateTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static final String REDMINE_BASIC_PRIORITY = "Степень приоритета 3 (Стандартная проблема)";
    public static final int REDMINE_CUSTOM_FIELD_ID = 89;
    public static final String COMMENT_PROTEI_USER_PREFIX = "PROTEI";
}
