package ru.protei.portal.core.model.dict;

import static ru.protei.portal.core.model.dict.En_Privilege.*;

/**
 * Категории привилегий
 */
public enum En_PrivilegeEntity {
    PROFILE,
    ISSUE,
    REGION,
    PRODUCT,
    PROJECT,
    EQUIPMENT,
    COMPANY,
    CONTACT,
    ACCOUNT,
    ROLE,
    OFFICIAL,
    DASHBOARD;

    public static En_Privilege[] getPrivileges(En_PrivilegeEntity category) {
        switch ( category ) {
            case PROFILE:
                return profilePrivileges;
            case ISSUE:
                return issuePrivileges;
            case REGION:
                return regionPrivileges;
            case PROJECT:
                return projectPrivileges;
            case COMPANY:
                return companyPrivileges;
            case PRODUCT:
                return productPrivileges;
            case CONTACT:
                return contactPrivileges;
            case ACCOUNT:
                return accountPrivileges;
            case EQUIPMENT:
                return equipmentPrivileges;
            case ROLE:
                return rolePrivileges;
            case OFFICIAL:
                return officialPrivileges;
            case DASHBOARD:
                return dashboardPrivileges;
        }

        return null;
    }

    private static final En_Privilege[] profilePrivileges = {
            COMMON_PROFILE_EDIT, COMMON_PROFILE_VIEW
    };

    private static final En_Privilege[] issuePrivileges = {
            ISSUE_CREATE, ISSUE_EDIT, ISSUE_VIEW, ISSUE_EXPORT, ISSUE_REPORT
    };

    private static final En_Privilege[] regionPrivileges = {
            REGION_EDIT, REGION_VIEW, REGION_EXPORT, REGION_EXPORT
    };

    private static final En_Privilege[] projectPrivileges = {
            PROJECT_CREATE, PROJECT_EDIT, PROJECT_VIEW
    };

    private static final En_Privilege[] companyPrivileges = {
            COMPANY_CREATE, COMPANY_EDIT, COMPANY_VIEW
    };

    private static final En_Privilege[] productPrivileges = {
            PRODUCT_CREATE, PRODUCT_EDIT, PRODUCT_VIEW
    };

    private static final En_Privilege[] contactPrivileges = {
            CONTACT_CREATE, CONTACT_EDIT, CONTACT_VIEW
    };

    private static final En_Privilege[] accountPrivileges = {
            ACCOUNT_CREATE, ACCOUNT_EDIT, ACCOUNT_VIEW
    };

    private static final En_Privilege[] equipmentPrivileges = {
            EQUIPMENT_CREATE, EQUIPMENT_EDIT, EQUIPMENT_VIEW
    };

    private static final En_Privilege[] rolePrivileges = {
            ROLE_CREATE, ROLE_EDIT, ROLE_VIEW
    };

    private static final En_Privilege[] officialPrivileges = {
            OFFICIAL_VIEW, OFFICIAL_EDIT
    };

    private static final En_Privilege[] dashboardPrivileges = {
            DASHBOARD_VIEW
    };
}
