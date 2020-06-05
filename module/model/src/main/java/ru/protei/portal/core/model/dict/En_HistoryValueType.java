package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_HistoryValueType implements HasId {
    ADD_TO_PLAN(0),
    CHANGE_PLAN(1),
    REMOVE_FROM_PLAN(2),
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
