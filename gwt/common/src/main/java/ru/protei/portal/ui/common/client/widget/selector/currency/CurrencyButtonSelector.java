package ru.protei.portal.ui.common.client.widget.selector.currency;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Currency;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class CurrencyButtonSelector extends ButtonSelector<En_Currency> {

    @Inject
    public void init(Lang lang) {
        setDisplayOptionCreator(value -> new DisplayOption(value == null ? defaultValue : value.getCode()));
        fillOptions();
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    private void fillOptions() {
        if (defaultValue != null) {
            addOption(null);
        }
        for (En_Currency value : En_Currency.values()) {
            addOption(value);
        }
    }

    private String defaultValue;
}
