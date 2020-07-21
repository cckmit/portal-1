package ru.protei.portal.core.model.dict;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    RECEPTIVITY_CENTER(11),
    HARDWARE_CURATOR(12),
    SOFTWARE_CURATOR(13),
    INTRO_NEW_TECH_SOLUTIONS(14),
    LIABLE_FOR_AUTO_TESTING(15),
    TECH_SUPPORT_CURATOR(16),
    PRODUCT_ASSEMBLER(17),
    SUPPLY_PREPARATION(18),
    ENGINEER_DOC_DEV(19),
    TECH_DOC_DEV(20),
    SOFTWARE_DOC_DEV(21),
    LIABLE_FOR_CERTIFICATION(22),
    OKR_ESCORT(23),
    QUALITY_CONTROL_SMK(24),
    CUSTOMER_INTEGRATION(25);

    private En_DevUnitPersonRoleType( int typeId ) {
        this.id = typeId;
    }

    private final int id;
    private static final List<En_DevUnitPersonRoleType> projectRoles = Collections.unmodifiableList( Arrays.asList(
            HEAD_MANAGER, HARDWARE_CURATOR, SOFTWARE_CURATOR, INTRO_NEW_TECH_SOLUTIONS, LIABLE_FOR_AUTO_TESTING,
            TECH_SUPPORT_CURATOR, PRODUCT_ASSEMBLER, SUPPLY_PREPARATION, ENGINEER_DOC_DEV, TECH_DOC_DEV, SOFTWARE_DOC_DEV,
            LIABLE_FOR_CERTIFICATION, OKR_ESCORT, QUALITY_CONTROL_SMK, CUSTOMER_INTEGRATION ) );

    private static final List<En_DevUnitPersonRoleType> ampluaRoles = Collections.unmodifiableList( Arrays.asList(
            DECISION_CENTER, CHIEF_DECISION_MAKER, KEEPER, TECH_SPECIALIST, INFLUENCE_MAKER, CHIEF_INFLUENCE_MAKER,
            ECONOMIST, WELL_WISHER, RECEPTIVITY_CENTER
    ) );

    public int getId() {
        return id;
    }

    public static En_DevUnitPersonRoleType forId (int id) {
        for (En_DevUnitPersonRoleType it : En_DevUnitPersonRoleType.values())
            if (it.getId() == id)
                return it;

        return null;
    }

    public static boolean isProjectRole(En_DevUnitPersonRoleType type) {
        return projectRoles.contains( type );
    }

    public static List<En_DevUnitPersonRoleType> getProjectRoles() {
        return projectRoles;
    }
}
