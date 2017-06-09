package ru.protei.portal.core.model.dict;

/**
 * Роли пользователей
 */
public enum En_UserRole {
    EMPLOYEE(1),
    CRM_ADMIN(2),
    CRM_USER(3),
    CRM_CLIENT(4),
    DN_ADMIN(5);

    En_UserRole (int roleId) {
        this.id = roleId;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static En_UserRole find (int id) {
        for (En_UserRole ur : En_UserRole.values())
            if (ur.id == id)
                return ur;

        return null;
    }
}
