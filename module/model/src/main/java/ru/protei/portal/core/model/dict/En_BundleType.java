package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_BundleType implements HasId {

    LINKED_WITH(1),
    PARENT_FOR(2),
    SUBTASK(3);

    private final int id;

    En_BundleType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
