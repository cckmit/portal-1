package ru.protei.portal.ui.common.client.widget.selector.contract;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

public class ContractMultiSelector extends MultipleInputSelector<Contract> implements SelectorWithModel<Contract> {

    @Inject
    public void init(ContractModel model, Lang lang) {
        this.lang = lang;
        setSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
    }

    @Override
    public void fillOptions(List<Contract> options) {
        clearOptions();
        options.forEach(option -> addOption(lang.contractNum(option.getNumber()), option));
    }

    private Lang lang;
}
