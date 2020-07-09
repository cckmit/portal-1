package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_HistoryType implements HasId {
    PLAN(0),
    ;

    En_HistoryType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private int id;
}
