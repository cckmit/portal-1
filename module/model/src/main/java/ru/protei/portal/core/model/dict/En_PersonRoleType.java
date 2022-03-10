package ru.protei.portal.core.model.dict;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Роль человека в команде
 */
public enum En_PersonRoleType {
    HEAD_MANAGER(1,1),
    DEPLOY_MANAGER(2,2),
    HARDWARE_CURATOR(12, 3),
    SOFTWARE_CURATOR(13, 4),
    INTRO_NEW_TECH_SOLUTIONS(14, 5),
    LIABLE_FOR_AUTO_TESTING(15, 6),
    TECH_SUPPORT_CURATOR(16, 7),
    PRODUCT_ASSEMBLER(17, 8),
    SUPPLY_PREPARATION(18, 9),
    ENGINEER_DOC_DEV(19, 10),
    TECH_DOC_DEV(20, 11),
    SOFTWARE_DOC_DEV(21, 12),
    LIABLE_FOR_CERTIFICATION(22, 13),
    OKR_ESCORT(23, 14),
    QUALITY_CONTROL_SMK(24, 15),
    CUSTOMER_INTEGRATION(25, 16),
    PRESALE_MANAGER(26, 17),
    BUSINESS_ANALYTICS_ARCHITECTURE(27, 18),
    PROJECT_DOCUMENTATION(28, 19),
    PRODUCT_MANAGER(29, 20),
    DEVELOPMENT(30, 21),
    PRESALE_HEAD_MANAGER(31, 22),
    DEPLOY_HEAD_MANAGER(32, 23),
    DELIVERY_PACKING(33, 24),
    SPECIAL_CHECK_SPECIAL_RESEARCH(34, 25),
    EQUIPMENT_SETUP(35, 26),
    ENTRANCE_CONTROL(36, 27),
    QAD_DOCUMENTATION(37, 28),
    OPERATIONAL_DOCUMENTATION(38, 29),
    AUTOMATIC_MOUNTING(39, 31),
    MANUAL_MOUNTING(40, 32),
    OUTPUT_CONTROL(41, 33),
    EQUIPMENT_ASSEMBLY(42, 30);

    En_PersonRoleType(int typeId, int order) {
        this.id = typeId;
        this.order = order;
    }

    private final int id;
    private final int order;

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

    private static final List<En_PersonRoleType> cardBatchRoles = Collections.unmodifiableList( Arrays.asList(
            EQUIPMENT_ASSEMBLY, AUTOMATIC_MOUNTING, MANUAL_MOUNTING, OUTPUT_CONTROL ) );

    public int getId() {
        return id;
    }

    public int getOrder() {
        return order;
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

    public static boolean isCardBatchRole(En_PersonRoleType type) {
        return cardBatchRoles.contains(type);
    }

    public static List<En_PersonRoleType> getProjectRoles() {
        return projectRoles;
    }

    public static List<En_PersonRoleType> getCardBatchRoles() {
        return cardBatchRoles;
    }
}
