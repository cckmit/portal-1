package ru.protei.portal.core.model.dict;

public enum En_DecimalNumberEntityType {
    EQUIPMENT(0),
    DOCUMENT(1);

    private final int id;

    En_DecimalNumberEntityType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
