package ru.protei.portal.core.model.dict;

public enum En_CaseCommentPrivacyType {
    PUBLIC(0),
    PRIVATE_CUSTOMERS(1),
    PRIVATE(2);

    private int id;
    En_CaseCommentPrivacyType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
