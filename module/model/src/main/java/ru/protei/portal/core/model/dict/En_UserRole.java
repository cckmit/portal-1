package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 26.05.17.
 */
public enum En_UserRole {
    EMPLOYEE (1),
    CRM_ADMIN(2),
    CRM_USER(3),
    CRM_CLIENT(4);

    En_UserRole (int roleId) {
        this.id = roleId;
    }

    private final int id;

    public int getId() {
        return id;
    }
}
