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
    DASHBOARD,
    ISSUE_COMPANY,
    ISSUE_PRODUCT,
    ISSUE_MANAGER,
    ISSUE_PRIVACY,
    DASHBOARD_ALL_COMPANIES;

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
            case ISSUE_COMPANY:
                return issueCompanyPrivileges;
            case ISSUE_PRODUCT:
                return issueProductPrivileges;
            case ISSUE_MANAGER:
                return issueManagerPrivileges;
            case ISSUE_PRIVACY:
                return issuePrivacyPrivileges;
            case DASHBOARD_ALL_COMPANIES:
                return dashboardAllCompaniesPrivileges;
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

    private static final En_Privilege[] issueCompanyPrivileges = {
        ISSUE_COMPANY_EDIT
    };

    private static final En_Privilege[] issueProductPrivileges = {
        ISSUE_PRODUCT_EDIT
    };

    private static final En_Privilege[] issueManagerPrivileges = {
        ISSUE_MANAGER_EDIT
    };

    private static final En_Privilege[] issuePrivacyPrivileges = {
        ISSUE_PRIVACY_VIEW
    };

    private static final En_Privilege[] dashboardAllCompaniesPrivileges = {
        DASHBOARD_ALL_COMPANIES_VIEW
    };
}
