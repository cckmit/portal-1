package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_PcbOrderType implements HasId {

    CARD(1),
    STENCIL(2),
    FRONT_PANEL(3);

    private final int id;

    En_PcbOrderType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
