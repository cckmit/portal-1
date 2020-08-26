package ru.protei.portal.ui.common.client.widget.selector.contract.state;

import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;

public class ContractStatesModel implements SelectorModel<En_ContractState> {

    @Override
    public En_ContractState get(int elementIndex) {
        try {
            return En_ContractState.values()[elementIndex];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
