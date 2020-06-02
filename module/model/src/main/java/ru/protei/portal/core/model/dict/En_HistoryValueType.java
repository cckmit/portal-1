package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_HistoryValueType implements HasId {
    CHANGE_PLAN(0),
    ;

    En_HistoryValueType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private int id;
}
