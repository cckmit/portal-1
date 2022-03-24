package ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype;

import ru.protei.portal.core.model.ent.CalculationType;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class CalculationTypeSelector extends FormPopupSingleSelector<CalculationType> {

    public CalculationTypeSelector() {
        setSearchEnabled(true);
        setHasNullValue(true);
        setDefaultValue(lang.contractCalculationTypeNotDefined());
        setItemRenderer(option -> option == null ? defaultValue : option.getName());
    }
}
