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

    OFFICIAL_VIEW(OFFICIAL, VIEW),
    OFFICIAL_EDIT(OFFICIAL, EDIT),

    DASHBOARD_VIEW(DASHBOARD, VIEW),

    ISSUE_COMPANY_EDIT( ISSUE_COMPANY, EDIT ),
    ISSUE_PRODUCT_EDIT( ISSUE_PRODUCT, EDIT ),
    ISSUE_MANAGER_EDIT( ISSUE_MANAGER, EDIT ),
    ISSUE_PRIVACY_VIEW( ISSUE_PRIVACY, VIEW );

    private final En_PrivilegeEntity entity;
    private final En_PrivilegeAction action;

    En_Privilege( En_PrivilegeEntity entity, En_PrivilegeAction action ) {
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
            if ( privilege.action.equals( action ) && privilege.entity.equals( entity ) ) {
                return privilege;
            }
        }

        return null;
    }
}
