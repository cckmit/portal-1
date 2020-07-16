package ru.protei.portal.ui.common.client.widget.selector.contractor.country;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ContractorCountry;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class ContractorCountrySelector extends ButtonPopupSingleSelector<ContractorCountry> {
    @Inject
    public void init( ContractorCountryModel model ) {
        this.model = model;
        setAsyncModel( model );
        setItemRenderer( option -> option == null ? defaultValue : option.getName() );
        setValidation(true);
        setHideSelectedFromChose(true);
        setDefaultValue(lang.contractContractorCountryPlaceholder());
    }

    public void setOrganization(String organization) {
        model.setOrganization(organization);
        model.clean();
    }
    ContractorCountryModel model;

}
