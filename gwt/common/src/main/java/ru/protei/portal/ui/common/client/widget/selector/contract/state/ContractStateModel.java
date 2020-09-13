package ru.protei.portal.ui.common.client.widget.selector.contract.state;

import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.Arrays;

public class ContractStateModel implements SelectorModel<En_ContractState> {
    @Override
    public void onSelectorLoad(SelectorWithModel<En_ContractState> selector) {
        if (selector == null) {
            return;
        }

        if (selector.getValues() == null || selector.getValues().isEmpty()) {
            selector.fillOptions(Arrays.asList(En_ContractState.values()));
        }
    }
}
