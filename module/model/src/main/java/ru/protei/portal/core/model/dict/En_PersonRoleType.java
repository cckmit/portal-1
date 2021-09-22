package ru.protei.portal.core.model.dict;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Роль человека в команде
 */
public enum En_PersonRoleType {
    HEAD_MANAGER(1),
    DEPLOY_MANAGER(2),
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
    CUSTOMER_INTEGRATION(25),
    PRESALE_MANAGER(26),
    BUSINESS_ANALYTICS_ARCHITECTURE(27),
    PROJECT_DOCUMENTATION(28),
    PRODUCT_MANAGER(29),
    DEVELOPMENT(30),
    PRESALE_HEAD_MANAGER(31),
    DEPLOY_HEAD_MANAGER(32),
    DELIVERY_PACKING(33),
    SPECIAL_CHECK_SPECIAL_RESEARCH(34),
    EQUIPMENT_SETUP(35),
    ENTRANCE_CONTROL(36),
    QAD_DOCUMENTATION(37),
    OPERATIONAL_DOCUMENTATION(38);

    En_PersonRoleType(int typeId ) {
        this.id = typeId;
    }

    private final int id;

    private static final List<En_PersonRoleType> projectRoles = Collections.unmodifiableList( Arrays.asList(
            HEAD_MANAGER, DEPLOY_MANAGER, HARDWARE_CURATOR, SOFTWARE_CURATOR, INTRO_NEW_TECH_SOLUTIONS, LIABLE_FOR_AUTO_TESTING,
            TECH_SUPPORT_CURATOR, PRODUCT_ASSEMBLER, SUPPLY_PREPARATION, ENGINEER_DOC_DEV, TECH_DOC_DEV, SOFTWARE_DOC_DEV,
            LIABLE_FOR_CERTIFICATION, OKR_ESCORT, QUALITY_CONTROL_SMK, CUSTOMER_INTEGRATION, PRESALE_MANAGER,
            BUSINESS_ANALYTICS_ARCHITECTURE, PROJECT_DOCUMENTATION, PRODUCT_MANAGER, DEVELOPMENT, PRESALE_HEAD_MANAGER,
            DEPLOY_HEAD_MANAGER, DELIVERY_PACKING, SPECIAL_CHECK_SPECIAL_RESEARCH, EQUIPMENT_SETUP, ENTRANCE_CONTROL,
            QAD_DOCUMENTATION, OPERATIONAL_DOCUMENTATION ) );

    private static final List<En_PersonRoleType> deliveryRoles = Collections.unmodifiableList( Arrays.asList(
            HEAD_MANAGER, HARDWARE_CURATOR, SOFTWARE_CURATOR, LIABLE_FOR_AUTO_TESTING, TECH_SUPPORT_CURATOR,
            PRODUCT_ASSEMBLER, ENGINEER_DOC_DEV, QUALITY_CONTROL_SMK, PROJECT_DOCUMENTATION, DELIVERY_PACKING,
            SPECIAL_CHECK_SPECIAL_RESEARCH, EQUIPMENT_SETUP, ENTRANCE_CONTROL, QAD_DOCUMENTATION, OPERATIONAL_DOCUMENTATION ) );

    public int getId() {
        return id;
    }

    public static En_PersonRoleType forId (int id) {
        for (En_PersonRoleType it : En_PersonRoleType.values())
            if (it.getId() == id)
                return it;

        return null;
    }

    public static boolean isProjectRole(En_PersonRoleType type) {
        return projectRoles.contains( type );
    }

    public static boolean isDeliveryRole(En_PersonRoleType type) {
        return deliveryRoles.contains(type);
    }

    public static List<En_PersonRoleType> getProjectRoles() {
        return projectRoles;
    }
}
