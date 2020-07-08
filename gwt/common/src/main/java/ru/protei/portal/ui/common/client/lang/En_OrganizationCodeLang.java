package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Organization;


/**
 * Код организации разработчика оборудования
 */
public class En_OrganizationCodeLang {

    public String getName( En_Organization value ) {
        switch (value) {
            case PROTEI:
                return lang.equipmentOrganizationCodePAMR();
            case PROTEI_ST:
                return lang.equipmentOrganizationCodePDRA();

            default:
                return lang.unknownField();
        }
    }

    public String getCompanyName( En_Organization value ) {
        switch (value) {
            case PROTEI:
                return lang.organizationProtei();
            case PROTEI_ST:
                return lang.organizationProteiST();

            default:
                return lang.unknownField();
        }
    }

    @Inject
    Lang lang;
}
