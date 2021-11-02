package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_PcbOrderState implements HasId {

    RECEIVED(1),
    ACCEPTED(2),
    SENT(3);

    private final int id;

    En_PcbOrderState(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
