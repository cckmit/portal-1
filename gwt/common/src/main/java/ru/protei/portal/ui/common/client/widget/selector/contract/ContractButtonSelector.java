package ru.protei.portal.ui.common.client.widget.selector.contract;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

import java.util.List;

public class ContractButtonSelector extends ButtonSelector<EntityOption> implements SelectorWithModel<EntityOption> {

    @Inject
    public void init(ContractModel model, Lang lang) {
        setSelectorModel(model);
        setDisplayOptionCreator(value -> {
            if (value == null) {
                return new DisplayOption(defaultValue == null ? lang.selectValue() : defaultValue);
            } else {
                return new DisplayOption(lang.contractNum(value.getDisplayText()));
            }
        });
    }

    @Override
    public void fillOptions(List<EntityOption> options) {
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
