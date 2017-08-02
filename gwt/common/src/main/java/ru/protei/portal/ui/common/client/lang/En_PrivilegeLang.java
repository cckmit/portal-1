package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;


public class En_PrivilegeLang {

    public String getName( En_Privilege value ) {
        switch (value) {
            case COMMON_PROFILE_VIEW:
                return lang.privilegeView();
            case COMMON_PROFILE_EDIT:
                return lang.privilegeEdit();

            case ISSUE_VIEW:
                return lang.privilegeView();
            case ISSUE_EDIT:
                return lang.privilegeEdit();
            case ISSUE_CREATE:
                return lang.privilegeCreate();
            case ISSUE_REPORT:
                return lang.privilegeReport();
            case ISSUE_EXPORT:
                return lang.privilegeExport();

            case REGION_VIEW:
                return lang.privilegeView();
            case REGION_EDIT:
                return lang.privilegeEdit();
            case REGION_REPORT:
                return lang.privilegeReport();
            case REGION_EXPORT:
                return lang.privilegeExport();

            case PROJECT_VIEW:
                return lang.privilegeView();
            case PROJECT_EDIT:
                return lang.privilegeEdit();
            case PROJECT_CREATE:
                return lang.privilegeCreate();

            case COMPANY_VIEW:
                return lang.privilegeView();
            case COMPANY_EDIT:
                return lang.privilegeEdit();
            case COMPANY_CREATE:
                return lang.privilegeCreate();

            case PRODUCT_VIEW:
                return lang.privilegeView();
            case PRODUCT_EDIT:
                return lang.privilegeEdit();
            case PRODUCT_CREATE:
                return lang.privilegeCreate();

            case CONTACT_VIEW:
                return lang.privilegeView();
            case CONTACT_EDIT:
                return lang.privilegeEdit();
            case CONTACT_CREATE:
                return lang.privilegeCreate();

            case ACCOUNT_VIEW:
                return lang.privilegeView();
            case ACCOUNT_EDIT:
                return lang.privilegeEdit();
            case ACCOUNT_CREATE:
                return lang.privilegeCreate();
            case ACCOUNT_REMOVE:
                return lang.privilegeRemove();

            case EQUIPMENT_VIEW:
                return lang.privilegeView();
            case EQUIPMENT_EDIT:
                return lang.privilegeEdit();
            case EQUIPMENT_CREATE:
                return lang.privilegeCreate();
            case EQUIPMENT_REMOVE:
                return lang.privilegeRemove();

            case ROLE_VIEW:
                return lang.privilegeView();
            case ROLE_EDIT:
                return lang.privilegeEdit();
            case ROLE_CREATE:
                return lang.privilegeCreate();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
