package ru.protei.portal.ui.common.client.widget.selector.decimalnumber;

import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.common.client.common.DecimalNumberFormatter;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.hints.InputWithHintsSelector;

public class DecimalNumberInput extends InputWithHintsSelector<DecimalNumber> {
    DecimalNumberInput() {
        setDisplayOptionCreator(dn -> new DisplayOption(DecimalNumberFormatter.formatNumber(dn)));
    }
}
