package ru.protei.portal.ui.common.client.widget.selector.contract.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.ui.common.client.lang.En_ContractStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ContractStatesMultiSelector extends InputPopupMultiSelector<En_ContractState> {

    @Inject
    public void init(Lang lang, En_ContractStateLang stateLang) {
        setModel(elementIndex -> {
            try {
                return En_ContractState.values()[elementIndex];
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        });
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(stateLang::getName);
    }
}
