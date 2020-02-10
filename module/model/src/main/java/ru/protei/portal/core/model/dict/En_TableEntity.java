package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_TableEntity implements HasId {
    COLUMN(0),
    ROW(1),
    ;

    En_TableEntity(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    private final int id;
}
