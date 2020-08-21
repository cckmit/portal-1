package ru.protei.portal.ui.contract.client.widget.selector.multi;

import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;

public class ContractTypesModel implements SelectorModel<En_ContractType> {

    @Override
    public En_ContractType get(int elementIndex) {
        try {
            return En_ContractType.values()[elementIndex];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
