package ru.protei.portal.app.portal.server.portal1794;

import ru.protei.portal.core.model.ent.Person;

import java.util.Map;

public class model {
    static class UserSpentTime {
        Person user;
        Integer spentTime;
    }



    enum WorkType {
        Bug,            // баги
        Product,        // продуктовое развитие
        Customer,       // доработки для заказчика
        Tech,           // улучшение эксплуатационных свойств
        Environment,    // Инфраструктура
        Etc,            // Разное
        Unknown;        // неопределено
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
}


