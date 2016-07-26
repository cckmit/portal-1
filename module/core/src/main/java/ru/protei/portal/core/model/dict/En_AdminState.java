package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 26.07.16.
 */
public enum En_AdminState {

    LOCKED (1),
    UNLOCKED(2);

    En_AdminState (int id) {
        this.id = id;
    }

    private final int id;


    public int getId() {
        return id;
    }
}
