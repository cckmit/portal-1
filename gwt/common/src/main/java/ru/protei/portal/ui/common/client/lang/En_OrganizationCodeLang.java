package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_OrganizationCode;


/**
 * Код организации разработчика оборудования
 */
public class En_OrganizationCodeLang {

    public String getName( En_OrganizationCode value ) {
        switch (value) {
            case PAMR:
                return lang.equipmentOrganizationCodePAMR();
            case PDRA:
                return lang.equipmentOrganizationCodePDRA();

            default:
                return lang.unknownField();
        }
    }

    public String getCompanyName( En_OrganizationCode value ) {
        switch (value) {
            case PAMR:
                return lang.equipmentOrganizationProtei();
            case PDRA:
                return lang.equipmentOrganizationProteiST();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
