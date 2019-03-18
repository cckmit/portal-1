package ru.protei.portal.ui.common.client.widget.selector.contract;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ContractButtonSelector extends ButtonSelector<Contract> implements SelectorWithModel<Contract> {

    @Inject
    public void init(ContractModel model, Lang lang) {
        setSelectorModel(model);
        setDisplayOptionCreator(value -> {
            if (value == null) {
                return new DisplayOption(defaultValue == null ? lang.selectValue() : defaultValue);
            } else {
                return new DisplayOption(lang.contractNum(value.getNumber()));
            }
        });
    }

    @Override
    public void fillOptions(List<Contract> options) {
        clearOptions();
        if (hasNullValue) {
            addOption(null);
        }
        options.forEach(this::addOption);
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    protected String defaultValue = null;
}
