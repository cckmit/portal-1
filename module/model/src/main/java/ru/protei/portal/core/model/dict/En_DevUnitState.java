package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 23.05.16.
 */
public enum En_DevUnitState {

    ACTIVE(1),
    DEPRECATED(2);

    private En_DevUnitState (int id) {
        this.id = id;
    }

    private int id;

    public int getId() {
        return id;
    }


    public static En_DevUnitState forId (int state) {
        if (state == ACTIVE.getId())
            return ACTIVE;
        else if (state == DEPRECATED.getId())
            return DEPRECATED;
        else
            return null;
    }
}
