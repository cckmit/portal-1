package ru.protei.portal.app.portal.server.portal1794;

import ru.protei.portal.core.model.ent.Person;

import java.util.Map;

public class model {
    static class UserSpentTime {
        Person user;
        Integer spentTime;
    }

    enum WorkType {
        BUG,            // баги
        PRODUCT,        // продуктовое развитие
        CUSTOMER,       // доработки для заказчика
        TECH,           // улучшение эксплуатационных свойств
        ENVIRONMENT,    // Инфраструктура
        ETC,            // Разное
        UNKNOWN;        // неопределенно
    }

    enum YTCustomField {
        Type("Type"),
        State("State"),
        Customer("Заказчик"),
        Assignee("Assignee"),
        spentTime("Затраченное время"),
        PortalProject("Проекты CRM"),
        changeWorkType("Классификатор задач"),
        changeWorkBase("Основание для доработки");

        String name;

        YTCustomField(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    static class WorkTimeItem {
        String customer;
        Integer projectId;
        String projectName;
        String contract;
        Map<WorkType, Integer> spentTime;
    }

    static class WorkItem {
        String user;
        String contract;
        String guarantee;
        String niokr;
        String nma;
        Integer spentTime;
        Integer planTime;
    }

    static class YTReportIssue {
        Person person;
        Long contractTime;
        Long guaranteeTime;
        Long niokrTime;
        Long nma;
    }
}


