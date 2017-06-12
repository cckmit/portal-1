package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PrivilegeCategory;
import ru.protei.portal.core.model.dict.En_SortField;


public class En_PrivilegeCategoryLang {

    public String getName( En_PrivilegeCategory value ) {
        switch (value) {
            case COMMON:
                return lang.privilegeCategoryCommon();
            case ISSUE:
                return lang.privilegeCategoryIssue();
            case REGION:
                return lang.privilegeCategoryRegion();
            case PROJECT:
                return lang.privilegeCategoryProject();
            case COMPANY:
                return lang.privilegeCategoryCompany();
            case PRODUCT:
                return lang.privilegeCategoryProduct();
            case CONTACT:
                return lang.privilegeCategoryContact();
            case ACCOUNT:
                return lang.privilegeCategoryAccount();
            case EQUIPMENT:
                return lang.privilegeCategoryEquipment();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
