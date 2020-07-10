package ru.protei.portal.ui.common.client.widget.selector.contractor.contractor;

import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.ContractorPair;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

import java.util.List;

public class ContractorSelector extends ButtonPopupSingleSelector<ContractorPair> {
    @Inject
    public void init( ContractorPairModel model ) {
        this.model = model;
        setModel( model );
        setItemRenderer( option -> option == null ? defaultValue : option.getContractorAPI().getFullname() );
        setValidation(true);
        setSearchEnabled(false);
        setHideSelectedFromChose(true);
        setDefaultValue(lang.contractContractorSelectorPlaceholder());
    }

    public void fill(List<ContractorPair> list) {
        model.fill(list);
    }

    ContractorPairModel model;
}
