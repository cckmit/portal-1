package ru.protei.portal.ui.contract.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractDatesType;
import ru.protei.portal.ui.common.client.lang.En_ContractDatesTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class ContractDatesTypeSelector extends ButtonSelector<En_ContractDatesType> {

    @Inject
    public void init() {
        setDisplayOptionCreator(o -> new DisplayOption(lang.getName(o)));
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();

        for(En_ContractDatesType value : En_ContractDatesType.values())
            addOption(value);
    }


    @Inject
    En_ContractDatesTypeLang lang;
}
