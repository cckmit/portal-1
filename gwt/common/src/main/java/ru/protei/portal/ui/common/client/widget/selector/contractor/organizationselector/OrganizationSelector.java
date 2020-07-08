package ru.protei.portal.ui.common.client.widget.selector.contractor.organizationselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Organization;
import ru.protei.portal.ui.common.client.lang.En_OrganizationCodeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class OrganizationSelector extends ButtonSelector<En_Organization> {

    @Inject
    public void init(Lang lang) {
        setValidation(true);
        setDisplayOptionCreator(value -> value == null ?
                new DisplayOption(lang.selectContractOrganization()) :
                new DisplayOption(codeLang.getCompanyName(value)));
        fillOptions();
    }

    private void fillOptions() {
        addOption(null);
        for (En_Organization value : En_Organization.values()) {
            addOption(value);
        }
    }

    @Inject
    En_OrganizationCodeLang codeLang;
}