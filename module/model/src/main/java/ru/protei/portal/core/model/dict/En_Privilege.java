package ru.protei.portal.core.model.dict;

import static ru.protei.portal.core.model.dict.En_PrivilegeAction.*;
import static ru.protei.portal.core.model.dict.En_PrivilegeEntity.*;

/**
 * Привилегии в системе
 */
public enum En_Privilege {
    COMMON_PROFILE_VIEW (PROFILE, VIEW),
    COMMON_PROFILE_EDIT (PROFILE, EDIT),

    ISSUE_VIEW (ISSUE, VIEW),
    ISSUE_EDIT (ISSUE, EDIT),
    ISSUE_CREATE (ISSUE, CREATE),
    ISSUE_REPORT (ISSUE, REPORT),
    ISSUE_EXPORT (ISSUE, EXPORT),

    REGION_VIEW (REGION, VIEW),
    REGION_EDIT (REGION, EDIT),
    REGION_REPORT (REGION, REPORT),
    REGION_EXPORT (REGION, EXPORT),

    PROJECT_VIEW (PROJECT, VIEW),
    PROJECT_EDIT (PROJECT, EDIT),
    PROJECT_CREATE (PROJECT, CREATE),

    COMPANY_VIEW (COMPANY, VIEW),
    COMPANY_EDIT (COMPANY, EDIT),
    COMPANY_CREATE (COMPANY, CREATE),

    PRODUCT_VIEW (PRODUCT, VIEW),
    PRODUCT_EDIT (PRODUCT, EDIT),
    PRODUCT_CREATE (PRODUCT, CREATE),

    CONTACT_VIEW (CONTACT, VIEW),
    CONTACT_EDIT (CONTACT, EDIT),
    CONTACT_CREATE (CONTACT, CREATE),
    CONTACT_REMOVE (CONTACT, REMOVE),

    ACCOUNT_VIEW (ACCOUNT, VIEW),
    ACCOUNT_EDIT (ACCOUNT, EDIT),
    ACCOUNT_CREATE (ACCOUNT, CREATE),
    ACCOUNT_REMOVE (ACCOUNT, REMOVE),

    EQUIPMENT_VIEW (EQUIPMENT, VIEW),
    EQUIPMENT_EDIT (EQUIPMENT, EDIT),
    EQUIPMENT_CREATE (EQUIPMENT, CREATE),
    EQUIPMENT_REMOVE (EQUIPMENT, REMOVE),

    ROLE_VIEW (ROLE, VIEW),
    ROLE_EDIT (ROLE, EDIT),
    ROLE_CREATE (ROLE, CREATE),
    ROLE_REMOVE (ROLE, REMOVE),

    OFFICIAL_VIEW(OFFICIAL, VIEW),
    OFFICIAL_EDIT(OFFICIAL, EDIT),

    DASHBOARD_VIEW(DASHBOARD, VIEW),

    DOCUMENT_VIEW(DOCUMENT, VIEW),
    DOCUMENT_EDIT(DOCUMENT, EDIT),
    DOCUMENT_CREATE(DOCUMENT, CREATE),
    DOCUMENT_REMOVE(DOCUMENT, REMOVE),

    DOCUMENT_TYPE_VIEW(DOCUMENT_TYPE, VIEW),
    DOCUMENT_TYPE_EDIT(DOCUMENT_TYPE, EDIT),
    DOCUMENT_TYPE_CREATE(DOCUMENT_TYPE, CREATE),

    @Deprecated
    DOCUMENT_TYPE_REMOVE(DOCUMENT_TYPE, REMOVE),

    SITE_FOLDER_VIEW (SITE_FOLDER, VIEW),
    SITE_FOLDER_EDIT (SITE_FOLDER, EDIT),
    SITE_FOLDER_CREATE (SITE_FOLDER, CREATE),
    SITE_FOLDER_REMOVE (SITE_FOLDER, REMOVE),

    // Набор дополнительных привилегий, которые вычисляются по scope и не пишутся в базу. Устанавливаются без action
    ISSUE_COMPANY_EDIT(ISSUE, null),
    ISSUE_PRODUCT_EDIT(ISSUE, null),
    ISSUE_MANAGER_EDIT(ISSUE, null),
    ISSUE_PRIVACY_VIEW(ISSUE, null),

    ISSUE_FILTER_COMPANY_VIEW(ISSUE, null),
    ISSUE_FILTER_MANAGER_VIEW(ISSUE, null),
    ISSUE_FILTER_PRODUCT_VIEW(ISSUE, null),

    CASE_STATES_VIEW (CASE_STATES, VIEW),
    CASE_STATES_EDIT (CASE_STATES, EDIT),
    CASE_STATES_CREATE (CASE_STATES, CREATE),
    CASE_STATES_REMOVE (CASE_STATES, REMOVE),

    @Deprecated
    DASHBOARD_ALL_COMPANIES_VIEW(DASHBOARD, VIEW);

    private final En_PrivilegeEntity entity;
    private final En_PrivilegeAction action;

    public final static En_Privilege [] DEFAULT_SCOPE_PRIVILEGES = {
            ISSUE_COMPANY_EDIT,
            ISSUE_PRODUCT_EDIT,
            ISSUE_MANAGER_EDIT,
            ISSUE_PRIVACY_VIEW,

            ISSUE_FILTER_COMPANY_VIEW,
            ISSUE_FILTER_MANAGER_VIEW,
            ISSUE_FILTER_PRODUCT_VIEW
    };

    private En_Privilege( En_PrivilegeEntity entity, En_PrivilegeAction action ) {
        this.entity = entity;
        this.action = action;
    }

    public En_PrivilegeEntity getEntity() {
        return entity;
    }

    public En_PrivilegeAction getAction() {
        return action;
    }

    public static En_Privilege findPrivilege( En_PrivilegeEntity entity, En_PrivilegeAction action ) {
        for ( En_Privilege privilege : values() ) {
            if ( privilege.action == null ) {
                continue;
            }

            if ( privilege.action.equals( action ) && privilege.entity.equals( entity ) ) {
                return privilege;
            }
        }

        return null;
    }

}
