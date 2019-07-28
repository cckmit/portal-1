package ru.protei.portal.core.model.dict;

public enum En_DocumentState {

    ACTIVE(1),
    DEPRECATED(2);

    private En_DocumentState (int id) {
            this.id = id;
        }

    private int id;

    public int getId() {
            return id;
        }


    public static En_DocumentState forId (int state) {
            return state == DEPRECATED.getId() ? DEPRECATED : ACTIVE;
        }

}
