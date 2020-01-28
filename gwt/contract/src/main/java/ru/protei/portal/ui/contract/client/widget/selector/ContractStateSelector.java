package ru.protei.portal.ui.contract.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.ui.common.client.lang.En_ContractStateLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;
import ru.protei.portal.ui.contract.client.widget.selector.model.ContractStateModel;

import java.util.List;

public class ContractStateSelector extends ButtonSelector<En_ContractState> implements SelectorWithModel<En_ContractState> {
    @Inject
    public void init(ContractStateModel model, En_ContractStateLang stateLang) {
        setSelectorModel(model);
        setDisplayOptionCreator(o -> new DisplayOption(o == null ? defaultValue : stateLang.getName(o)));
    }

    @Override
    public void fillOptions(List<En_ContractState> options) {
        clearOptions();

        if (defaultValue != null) {
            addOption(null);
        }

        options.forEach(this::addOption);
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private String defaultValue = null;
}
