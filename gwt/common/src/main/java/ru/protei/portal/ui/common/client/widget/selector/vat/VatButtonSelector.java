package ru.protei.portal.ui.common.client.widget.selector.vat;

import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class VatButtonSelector extends ButtonSelector<Long> {

    @Inject
    public void init(Lang lang) {
        setDisplayOptionCreator(value -> makeItemView(value, lang));
        setSearchEnabled(false);
    }

    public void setOptions(List<Long> options) {
        clearOptions();
        for (Long value : options) {
            addOption(value);
        }
    }

    private DisplayOption makeItemView(Long value, Lang lang) {
        String label = value == null
                ? lang.withoutVat()
                : lang.vat(value);
        return new DisplayOption(label);
    }
}
