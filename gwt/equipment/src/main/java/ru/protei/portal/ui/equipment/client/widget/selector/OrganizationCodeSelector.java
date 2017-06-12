package ru.protei.portal.ui.equipment.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.ui.common.client.lang.En_OrganizationCodeLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Селектор списка кодов организаций
 */
public class OrganizationCodeSelector extends ButtonSelector< En_OrganizationCode > {

    @Inject
    public void onInit() {
        fillOptions( new HashSet<>( Arrays.asList(En_OrganizationCode.values()) ));
        hasNullValue = false;
    }

    public void fillOptions( Set< En_OrganizationCode > availableValues ) {
        clearOptions();
        for ( En_OrganizationCode code : availableValues ) {
            addOption( organizationCodeLang.getName( code ), code );
        }
    }

    @Inject
    private En_OrganizationCodeLang organizationCodeLang;
}
