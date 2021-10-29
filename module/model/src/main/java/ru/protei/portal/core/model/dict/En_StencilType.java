package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_StencilType implements HasId {

    TOP(1),
    BOT(2),
    TOP_BOT(3);

    private final int id;

    En_StencilType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
