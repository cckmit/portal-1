package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PrivilegeEntity;


public class En_PrivilegeEntityLang {

    public String getName( En_PrivilegeEntity value ) {
        switch (value) {
            case PROFILE:
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
            case ROLE:
                return lang.privilegeCategoryRole();
            case OFFICIAL:
                return lang.privilegeCategoryOfficial();
            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
