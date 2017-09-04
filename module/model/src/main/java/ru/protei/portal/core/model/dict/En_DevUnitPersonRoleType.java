package ru.protei.portal.core.model.dict;

/**
 * Роль человека в команде
 */
public enum En_DevUnitPersonRoleType {
    HEAD_MANAGER(1),
    DEPLOY_MANAGER(2),
    ADS_MANAGER(3),
    SALES_MANAGER(4),
    ART_MANAGER(5);

    private En_DevUnitPersonRoleType( int typeId ) {
        this.id = typeId;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static En_DevUnitPersonRoleType forId (int id) {
        for (En_DevUnitPersonRoleType it : En_DevUnitPersonRoleType.values())
            if (it.getId() == id)
                return it;

        return null;
    }
}
