package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 23.05.16.
 */
public enum En_DevUnitType {
    COMPONENT(1),
    PRODUCT(2);

    private En_DevUnitType (int typeId) {
        this.id = typeId;
    }

    private final int id;

    public int getId() {
        return id;
    }
}
