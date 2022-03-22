package ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype;

import ru.protei.portal.core.model.ent.ContractCalculationType;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class ContractCalculationTypeSelector extends FormPopupSingleSelector<ContractCalculationType> {

    public ContractCalculationTypeSelector() {
        setItemRenderer(option -> option == null ? defaultValue : option.getName());
        setSearchEnabled(true);
    }
}
