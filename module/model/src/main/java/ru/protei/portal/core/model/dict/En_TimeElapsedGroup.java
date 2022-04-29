package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

public enum En_TimeElapsedGroup implements HasId {

    TYPE(1),
    DEPARTMENT(2),
    AUTHOR(3);

    En_TimeElapsedGroup(int id) {
        this.id = id;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static En_TimeElapsedGroup find (int id) {
        for (En_TimeElapsedGroup value : En_TimeElapsedGroup.values())
            if (value.id == id)
                return value;

        return null;
    }
}
