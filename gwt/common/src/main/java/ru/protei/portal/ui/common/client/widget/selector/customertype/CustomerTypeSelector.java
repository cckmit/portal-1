package ru.protei.portal.ui.common.client.widget.selector.customertype;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;


public class CustomerTypeSelector extends ButtonSelector<En_CustomerType> {

    @Inject
    public void init() {
        setDisplayOptionCreator(o -> new DisplayOption(lang.getName(o)));
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();

        for(En_CustomerType ct : En_CustomerType.values())
            addOption(ct);
    }

    @Inject
    private En_CustomerTypeLang lang;
}
