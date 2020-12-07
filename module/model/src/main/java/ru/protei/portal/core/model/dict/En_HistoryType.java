package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_HistoryType implements HasId {
    PLAN(0),
    TAG(1),
    CONTRACT_STATE(2),
    CASE_STATE(3),
    CASE_MANAGER(4),
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
