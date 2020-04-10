package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

/**
 * Created by michael on 26.07.16.
 */
public enum En_AdminState implements HasId {

    LOCKED (1),
    UNLOCKED(2);

    En_AdminState (int id) {
        this.id = id;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static En_AdminState find (int id) {
        for (En_AdminState as : En_AdminState.values())
            if (as.id == id)
                return as;

        return null;
    }
}
