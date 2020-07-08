package ru.protei.portal.ui.common.client.widget.selector.contractor.organizationselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Organization;
import ru.protei.portal.ui.common.client.lang.En_OrganizationCodeLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class OrganizationSelector extends ButtonPopupSingleSelector<En_Organization> {

    @Inject
    public void onInit(OrganizationModel model) {
        setModel( model );
        setItemRenderer( option -> option == null ? defaultValue : codeLang.getCompanyName(option) );
        setValidation(true);
        setSearchEnabled(false);
        setHideSelectedFromChose(true);
        setDefaultValue(lang.selectContractOrganization());
    }

    @Inject
    En_OrganizationCodeLang codeLang;
}