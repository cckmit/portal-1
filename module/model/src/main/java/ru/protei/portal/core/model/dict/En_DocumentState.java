package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_DocumentState implements HasId {

    ACTIVE(1),
    DEPRECATED(2);

    En_DocumentState (int id) {
            this.id = id;
    }

    private int id;

    public int getId() {
            return id;
    }
}
