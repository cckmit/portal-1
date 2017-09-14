package ru.protei.portal.core.model.dict;

/**
 * Роль человека в команде
 */
public enum En_DevUnitPersonRoleType {
    HEAD_MANAGER(1),
    DEPLOY_MANAGER(2),
    DECISION_CENTER(3),
    CHIEF_DECISION_MAKER(4),
    KEEPER(5),
    TECH_SPECIALIST(6),
    INFLUENCE_MAKER(7),
    CHIEF_INFLUENCE_MAKER(8),
    ECONOMIST(9),
    WELL_WISHER(10),
    RECEPTIVITY_CENTER(11);

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
