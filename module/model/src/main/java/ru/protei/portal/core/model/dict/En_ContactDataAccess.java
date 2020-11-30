package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

import java.io.Serializable;

public enum En_ContactDataAccess implements HasId, Serializable {

    PUBLIC(1),

    PRIVATE(2),

    INTERNAL(3),
    ;

    En_ContactDataAccess(int id) {
        this.id = id;
    }
    private final int id;
    public int getId() {
        return id;
    }
}
