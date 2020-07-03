package ru.protei.portal.ui.common.client.widget.selector.contractorcountry;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class ContractorCountrySelector extends ButtonPopupSingleSelector<String> {
    @Inject
    public void init( ContractorCountryModel companyModel ) {
        setAsyncModel( companyModel );
        setItemRenderer( option -> option == null ? defaultValue : option );
    }
}
