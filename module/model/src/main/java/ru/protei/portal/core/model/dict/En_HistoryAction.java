package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_HistoryAction implements HasId {
    ADD(0),
    CHANGE(1),
    REMOVE(2),
    ;

    En_HistoryAction(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private int id;
}
