package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_PcbOrderState implements HasId {

    SENT(1, "#42a5f5"),
    ACCEPTED(2, "#88027b"),
    RECEIVED(3, "#5d9e3c");

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
