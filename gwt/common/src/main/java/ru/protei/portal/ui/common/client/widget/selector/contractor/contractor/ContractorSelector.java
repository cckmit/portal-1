package ru.protei.portal.ui.common.client.widget.selector.contractor.contractor;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

import java.util.List;

public class ContractorSelector extends ButtonPopupSingleSelector<Contractor> {

    @Inject
    public void init( ContractorModel model ) {
        this.model = model;
        setModel( model );
        setItemRenderer( option -> option == null ? defaultValue : option.getFullName() );
        setValidation(true);
        setSearchEnabled(true);
        setHideSelectedFromChose(true);
        setDefaultValue(lang.contractContractorSelectorPlaceholder());
    }

    public void fill(List<Contractor> list) {
        clearSelector();
        model.fill(list);
    }

    ContractorModel model;
}
