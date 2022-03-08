package ru.protei.portal.ui.common.client.widget.selector.contract.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.lang.ContractStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ContractStatesMultiSelector extends InputPopupMultiSelector<CaseState> {

    @Inject
    public void init(ContractStateModel model, Lang lang, ContractStateLang stateLang) {
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(value -> stateLang.getName(value.getState()));
    }
}
