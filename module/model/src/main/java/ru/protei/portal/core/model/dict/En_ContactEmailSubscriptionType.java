package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

import java.io.Serializable;

public enum En_ContactEmailSubscriptionType implements HasId, Serializable {

    WITHOUT_SUBSCRIPTION(1),

    SUBSCRIPTION_TO_EMPLOYEE_REGISTRATION(2),

    SUBSCRIPTION_TO_END_OF_PROBATION(3),
    ;

    En_ContactEmailSubscriptionType(int id) {
        this.id = id;
    }
    private final int id;
    public int getId() {
        return id;
    }
}
