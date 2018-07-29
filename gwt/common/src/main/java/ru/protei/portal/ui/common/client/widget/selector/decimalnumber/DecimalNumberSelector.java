package ru.protei.portal.ui.common.client.widget.selector.decimalnumber;

import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class DecimalNumberSelector extends ButtonSelector<DecimalNumber> implements ModelSelector<DecimalNumber> {

    public DecimalNumberSelector() {
        setDisplayOptionCreator(dn -> new DisplayOption(DecimalNumberFormatter.formatNumber(dn)));
    }

    @Override
    public void fillOptions(List<DecimalNumber> options) {
        clearOptions();
        options.forEach(this::addOption);
    }
}
