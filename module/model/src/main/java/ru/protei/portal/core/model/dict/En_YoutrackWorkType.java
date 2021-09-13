package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_YoutrackWorkType implements HasId {
    NIOKR(1),
    NMA(2),
    CONTRACT(3),
    GUARANTEE(4)
    ;

    En_YoutrackWorkType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private final int id;
}
