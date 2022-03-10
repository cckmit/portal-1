package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_PcbOrderPromptness implements HasId {

    REGULAR(1, "#3f5fbd"),
    URGENT(2, "#906094"),
    VERY_URGENT(3, "#d85a5a");

    private final int id;
    private final String color;

    En_PcbOrderPromptness(int id, String color) {
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
