package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_PcbOrderState implements HasId {

    RECEIVED(1, "#5d9e3c"),
    ACCEPTED(2, "#88027b"),
    SENT(3, "#42a5f5");

    private final int id;
    private final String color;

    En_PcbOrderState(int id, String color) {
        this.id = id;
        this.color = color;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getColor() {
        return color;
    }
}
