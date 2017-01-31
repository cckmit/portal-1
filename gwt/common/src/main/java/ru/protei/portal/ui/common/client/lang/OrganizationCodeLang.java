package ru.protei.portal.ui.common.client.lang;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.shared.model.OrganizationCode;


/**
 * Код организации разработчика оборудования
 */
public class OrganizationCodeLang {

    public String getName( OrganizationCode value ) {
        switch (value) {
            case PAMR:
                return lang.equipmentOrganizationCodePAMR();
            case PDRA:
                return lang.equipmentOrganizationCodePDRA();

            default:
                return lang.unknownField();
        }
    }

    public String getCompanyName( OrganizationCode value ) {
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
