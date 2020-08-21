package ru.protei.portal.ui.contract.client.widget.selector.multi;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.ui.common.client.lang.En_ContractStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ContractStateMultiSelector extends InputPopupMultiSelector<En_ContractState> {

    @Inject
    public void init(ContractStatesModel model, Lang lang, En_ContractStateLang stateLang) {
        setModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(stateLang::getName);
    }
}
