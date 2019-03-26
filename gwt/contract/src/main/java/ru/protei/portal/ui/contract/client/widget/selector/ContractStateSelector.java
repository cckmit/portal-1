package ru.protei.portal.ui.contract.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.ui.common.client.lang.En_ContractStateLang;
import ru.protei.portal.ui.common.client.lang.En_ContractTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

public class ContractStateSelector extends ButtonSelector<En_ContractState> {

    @Inject
    public void init() {
        defaultValue = lang.selectorAny();
        setDisplayOptionCreator(o -> new DisplayOption(o == null ? defaultValue : stateLang.getName(o)));
        fillOptions();
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private void fillOptions() {
        clearOptions();

        if ( hasNullValue ) {
            addOption(null);
        }
        for(En_ContractState value : En_ContractState.values())
            addOption(value);
    }

    @Inject
    private Lang lang;
    @Inject
    private En_ContractStateLang stateLang;

    private String defaultValue = null;
}