package ru.protei.portal.ui.common.client.widget.selector.contract.calculationtype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CalculationType;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class CalculationTypesMultiSelector extends InputPopupMultiSelector<CalculationType> {

    @Inject
    public void init(Lang lang) {
        setItemRenderer(option -> option == null ? "" : option.getName());
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setSearchEnabled(true);
    }
}
