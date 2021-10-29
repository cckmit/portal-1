package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_PcbOrderPromptness implements HasId {

    REGULAR(1),
    URGENT(2),
    VERY_URGENT(3);

    private final int id;

    En_PcbOrderPromptness(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
