package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_Currency implements HasId {

    RUB(1, "RUB"),
    USD(2, "USD"),
    EUR(3, "EUR"),
    CONVENTIONAL_UNIT(4, "у.е."),
    ;

    En_Currency(int id, String code) {
        this.id = id;
        this.code = code;
    }

    private final int id;
    private final String code;

    @Override
    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}
