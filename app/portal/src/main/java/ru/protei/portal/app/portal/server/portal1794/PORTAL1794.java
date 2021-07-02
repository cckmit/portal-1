package ru.protei.portal.app.portal.server.portal1794;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.youtrack.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtSingleEnumIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.text.SimpleDateFormat;
import java.util.*;

public class PORTAL1794 {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);

        YoutrackApi api = ctx.getBean(YoutrackApi.class);
        try {
//            String query = String.format(" updated: %s .. %s", dateToYtString(new Date(2021-1900, Calendar.JUNE, 28)), dateToYtString(new Date(2021-1900, Calendar.JUNE, 28)));
            String query = "";
            Result<List< YtActivityItem >> activities = api.getActivitiesByQuery(query);
            activities.getData().forEach(System.out::println);
//            issueReports.getData().forEach(issue -> System.out.println(makeInfo(issue)));
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            ctx.destroy();
        }
    }

    static String dateToYtString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    static String makeInfo(YtIssue issue) {
        return String.format("id = %s, idReadable = %s, summary = %s, client = %s",
                issue.id,
                issue.idReadable,
                issue.summary,
                CollectionUtils.stream(issue.customFields).filter(field -> "Заказчик".equals(field.name))
                        .findAny()
                        .map(field -> ((YtSingleEnumIssueCustomField)field).value)
                        .filter(Objects::nonNull)
                        .map(field -> field.name).orElse("no client"));
    }

    static Set<String> homeCompany = new HashSet<>(Arrays.asList(null, "НТЦ Протей", "Нет заказчика", "Протей СТ"));
    static boolean isHomeCompany(String company) {
        return homeCompany.contains(company);
    }

    static model.WorkType makeWorkType(String workType, String type, String workBase) {
        switch (workType) {
            case "Баг": return model.WorkType.BUG;
            case "Заказчик": return model.WorkType.CUSTOMER;
            case "Инфраструктура": return model.WorkType.ENVIRONMENT;
            case "Развитие": return model.WorkType.PRODUCT;
            case "Улучшение": return model.WorkType.TECH;
            case "Разное": return model.WorkType.ETC;
            default: break;
        }

        if ("Bug".equals(type))  {
            return model.WorkType.BUG;
        }

        switch (workBase) {
            case "Плановое развитие": return model.WorkType.PRODUCT;
            case "* Совещания, управление и пр.": return model.WorkType.ETC;
            case "Рефакторинг": return model.WorkType.TECH;
            case "Контракт (в соответствии с пунктом ТЗ)": return model.WorkType.CUSTOMER;
            case "Инфраструктура (в том числе для ТП)": return model.WorkType.ENVIRONMENT;
            case "Квота Менеджера": return model.WorkType.CUSTOMER;
            case "Контракт (пункт ТЗ)": return model.WorkType.CUSTOMER;
            case "Квота Проекта": return model.WorkType.CUSTOMER;
            case "Демонстрация": return model.WorkType.ETC;
            default: break;
        }

        return model.WorkType.UNKNOWN;
    }
}
