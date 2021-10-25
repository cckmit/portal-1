package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_HistoryType implements HasId {
    PLAN(0),
    TAG(1),
    CONTRACT_STATE(2),
    CASE_STATE(3),
    CASE_MANAGER(4),
    CASE_IMPORTANCE(5),
    DEPARTURE_DATE(6),
    DELIVERY_STATE(7),
    MODULE_STATE(8),
    BUILD_DATE(9),
    CARD_BATCH_STATE(10),
    CARD_STATE(11),
    CARD_MANAGER(12),
    CARD_BATCH_IMPORTANCE(13),
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
