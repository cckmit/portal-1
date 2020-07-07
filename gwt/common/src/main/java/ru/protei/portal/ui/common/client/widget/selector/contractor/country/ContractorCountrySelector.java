package ru.protei.portal.ui.common.client.widget.selector.contractor.country;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class ContractorCountrySelector extends ButtonPopupSingleSelector<String> {
    @Inject
    public void init( ContractorCountryModel model ) {
        setAsyncModel( model );
        setItemRenderer( option -> option == null ? defaultValue : option );
    }
}
