package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 20.05.16.
 */
public enum En_CaseTermType {

    DEADLINE(1),
    WORKAROUND(2),
    FINAL(3);

    private En_CaseTermType (int id) {
        this.id = id;
    }

    private final int id;

    public int getId() {
        return id;
    }
}
