package ru.protei.portal.core.model.dict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Роль человека в команде
 */
public enum En_DevUnitPersonRoleType {
    HEAD_MANAGER(1, Type.PROJECT_TEAM),
    DEPLOY_MANAGER(2),
    DECISION_CENTER(3, Type.AMPLUA),
    CHIEF_DECISION_MAKER(4, Type.AMPLUA),
    KEEPER(5, Type.AMPLUA),
    TECH_SPECIALIST(6, Type.AMPLUA),
    INFLUENCE_MAKER(7, Type.AMPLUA),
    CHIEF_INFLUENCE_MAKER(8, Type.AMPLUA),
    ECONOMIST(9, Type.AMPLUA),
    WELL_WISHER(10, Type.AMPLUA),
    RECEPTIVITY_CENTER(11, Type.AMPLUA),
    HARDWARE_CURATOR(12, Type.PROJECT_TEAM),
    SOFTWARE_CURATOR(13, Type.PROJECT_TEAM),
    INTRO_NEW_TECH_SOLUTIONS(14, Type.PROJECT_TEAM),
    LIABLE_FOR_AUTO_TESTING(15, Type.PROJECT_TEAM),
    TECH_SUPPORT_CURATOR(16, Type.PROJECT_TEAM),
    PRODUCT_ASSEMBLER(17, Type.PROJECT_TEAM),
    SUPPLY_PREPARATION(18, Type.PROJECT_TEAM),
    ENGINEER_DOC_DEV(19, Type.PROJECT_TEAM),
    TECH_DOC_DEV(20, Type.PROJECT_TEAM),
    SOFTWARE_DOC_DEV(21, Type.PROJECT_TEAM),
    LIABLE_FOR_CERTIFICATION(22, Type.PROJECT_TEAM),
    OKR_ESCORT(23, Type.PROJECT_TEAM),
    QUALITY_CONTROL_SMK(24, Type.PROJECT_TEAM),
    CUSTOMER_INTEGRATION(25, Type.PROJECT_TEAM);

    private En_DevUnitPersonRoleType( int typeId, Type... types ) {
        this.id = typeId;
        this.types = Arrays.asList(types);
    }

    private final int id;
    private final List<Type> types;

    public int getId() {
        return id;
    }

    public static En_DevUnitPersonRoleType forId (int id) {
        for (En_DevUnitPersonRoleType it : En_DevUnitPersonRoleType.values())
            if (it.getId() == id)
                return it;

        return null;
    }

    public static List<En_DevUnitPersonRoleType> getAmpluaRoles() {
        List<En_DevUnitPersonRoleType> roles = new ArrayList<>();
        for (En_DevUnitPersonRoleType role : values()) {
            if (role.types.contains(Type.AMPLUA)) {
                roles.add(role);
            }
        }
        return roles;
    }

    public static List<En_DevUnitPersonRoleType> getProjectRoles() {
        List<En_DevUnitPersonRoleType> roles = new ArrayList<>();
        for (En_DevUnitPersonRoleType role : values()) {
            if (role.types.contains(Type.PROJECT_TEAM)) {
                roles.add(role);
            }
        }
        return roles;
    }

    private enum Type {
        AMPLUA,
        PROJECT_TEAM
    }
}
