package ru.protei.portal.ui.contract.client.widget.selector.button;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.ui.common.client.lang.En_ContractTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ContractTypeSelector extends ButtonSelector<En_ContractType> implements SelectorWithModel<En_ContractType> {

    @Inject
    public void init(ContractTypeModel model, En_ContractTypeLang typeLang) {
        setSelectorModel(model);
        setDisplayOptionCreator(o -> new DisplayOption(o == null ? defaultValue : typeLang.getName(o)));
    }

    @Override
    public void fillOptions(List<En_ContractType> options) {
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
