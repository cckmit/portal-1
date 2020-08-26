package ru.protei.portal.ui.common.client.widget.selector.contract.type;

import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.Arrays;

public class ContractTypeModel implements SelectorModel<En_ContractType> {
    @Override
    public void onSelectorLoad(SelectorWithModel<En_ContractType> selector) {
        if (selector == null) {
            return;
        }

        if (selector.getValues() == null || selector.getValues().isEmpty()) {
            selector.fillOptions(Arrays.asList(En_ContractType.values()));
        }
    }
}
