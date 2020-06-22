package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

import java.util.ArrayList;
import java.util.List;

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

    public static final List<En_HistoryValueType> PLANS = new ArrayList<>();
    private int id;

    static {
        PLANS.add(ADD_TO_PLAN);
        PLANS.add(CHANGE_PLAN);
        PLANS.add(REMOVE_FROM_PLAN);
    }
}
