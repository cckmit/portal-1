package ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractCalculationType;
import ru.protei.portal.ui.common.client.lang.En_ContractCalculationTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ContractCalculationTypesMultiSelector extends InputPopupMultiSelector<En_ContractCalculationType> {

    @Inject
    public void init(Lang lang, En_ContractCalculationTypeLang typeLang) {
        setModel(elementIndex -> {
            try {
                return En_ContractCalculationType.values()[elementIndex];
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        });
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(typeLang::getName);
        setSearchEnabled(false);
    }
}
