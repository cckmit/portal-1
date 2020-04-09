package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CompanyCategory;

public class En_CompanyCategoryLang {
    public String getName( En_CompanyCategory value ) {
        if (value == null)
            return "";

        switch (value) {
            case CUSTOMER:
                return lang.companyCategoryCustomer();
            case PARTNER:
                return lang.companyCategoryPartner();
            case SUBCONTRACTOR:
                return lang.companyCategorySubcontractor();
            case OFFICIAL:
                return lang.companyCategoryOfficial();
            case HOME:
                return lang.companyCategoryHome();
        }
        return lang.unknownField();
    }

    @Inject
    private Lang lang;
}
