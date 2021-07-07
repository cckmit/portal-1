package ru.protei.portal.app.portal.server.portal1794;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.MainConfiguration;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.youtrack.dto.customfield.issue.YtSingleEnumIssueCustomField;
import ru.protei.portal.core.model.youtrack.dto.issue.IssueWorkItem;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.text.SimpleDateFormat;
import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

public class PORTAL1794 {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
                CoreConfigurationContext.class, JdbcConfigurationContext.class, MainConfiguration.class);

        YoutrackApi api = ctx.getBean(YoutrackApi.class);
        PersonDAO personDAO = ctx.getBean(PersonDAO.class);
        try {
//            String query = String.format(" work date: %s .. %s work author: porubov", dateToYtString(new Date(2021-1900, Calendar.JUNE, 28)), dateToYtString(new Date(2021-1900, Calendar.JUNE, 28)));
//            String query = "";
//            Result<List< YtActivityItem >> activities = api.getActivities(
//                    new Date(2021-1900, Calendar.JUNE, 1),
//                    new Date(2021-1900, Calendar.JUNE, 30),
//                    0L, 1000L,
//                    YtActivityCategory.WorkItemDurationCategory
//                    );
            Result<List<IssueWorkItem>> result = api.getWorkItems(
                    new Date(2021-1900, Calendar.JUNE, 20),
                    new Date(2021-1900, Calendar.JUNE, 30),
                    0L, 2000L
            );
            HashMap<Person, PersonWithWorkItem> data = result.map(issueWorkItems -> stream(issueWorkItems)
                    .map(issueWorkItem -> {
                        PersonQuery query = new PersonQuery();
                        String email = issueWorkItem.author.email;
                        Person person = null;
                        if (isNotEmpty(email)) {
                            query.setEmail(email);
                            List<Person> persons = personDAO.getPersons(query);
                            if (persons.size() == 1) {
                                person = persons.get(0);
                            }
                        }
                        return new PersonWithWorkItem2(person != null ? person : new Person(),
                                issueWorkItem.issue.idReadable,
                                issueWorkItem.duration.minutes);
                    }).collect(HashMap::new, PORTAL1794::collectItems, PORTAL1794::mergeMap))
                    .getData();

            data.forEach((k, v) -> System.out.println(v));

//            issueReports.getData().forEach(issue -> System.out.println(makeInfo(issue)));
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            ctx.destroy();
        }
    }

    static HashMap<Person, PersonWithWorkItem> collectItems(HashMap<Person, PersonWithWorkItem> map, PersonWithWorkItem2 item2) {
        PersonWithWorkItem personWithWorkItem = map.compute(item2.person, (k, v) -> v == null ? new PersonWithWorkItem(item2.person) : v);
        personWithWorkItem.spentTime.compute(item2.issue, (k, v) -> item2.spentTime + ((v == null) ? 0 : v));
        return map;
    }

    static HashMap<Person, PersonWithWorkItem> mergeMap(HashMap<Person, PersonWithWorkItem> map1,
                                                        HashMap<Person, PersonWithWorkItem> map2) {
        return map1;
    }

    static class PersonWithWorkItem2 {
        Person person;
        String issue;
        Integer spentTime;

        public PersonWithWorkItem2(Person person, String issue, Integer spentTime) {
            this.person = person;
            this.issue = issue;
            this.spentTime = spentTime;
        }
    }

    static class PersonWithWorkItem {
        Person person;
        Map<String, Long> spentTime;

        public PersonWithWorkItem(Person person) {
            this.person = person;
            this.spentTime = new HashMap<>();
        }

        @Override
        public String toString() {
            return "PersonWithWorkItem{" +
                    "person=" + person +
                    ", spentTime=" + spentTime +
                    '}';
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
                stream(issue.customFields).filter(field -> "Заказчик".equals(field.name))
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
