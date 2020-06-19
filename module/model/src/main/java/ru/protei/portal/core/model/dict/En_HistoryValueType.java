package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

import java.util.ArrayList;
import java.util.List;

public enum En_HistoryValueType implements HasId {
    ADD_TO_PLAN(0) {
        @Override
        public En_Privilege getPrivilege() {
            return En_Privilege.PLAN_VIEW;
        }
    },
    CHANGE_PLAN(1) {
        @Override
        public En_Privilege getPrivilege() {
            return En_Privilege.PLAN_VIEW;
        }
    },
    REMOVE_FROM_PLAN(2) {
        @Override
        public En_Privilege getPrivilege() {
            return En_Privilege.PLAN_VIEW;
        }
    },
    ;

    En_HistoryValueType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public abstract En_Privilege getPrivilege();
    public static final List<En_HistoryValueType> PLANS = new ArrayList<>();
    private int id;

    static {
        PLANS.add(ADD_TO_PLAN);
        PLANS.add(CHANGE_PLAN);
        PLANS.add(REMOVE_FROM_PLAN);
    }
}
