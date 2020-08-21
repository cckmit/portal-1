package ru.protei.portal.ui.contract.client.widget.selector.multi;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.ui.common.client.lang.En_ContractTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ContractTypeMultiSelector extends InputPopupMultiSelector<En_ContractType> {

    @Inject
    public void init(ContractTypesModel model, Lang lang, En_ContractTypeLang typeLang) {
        setModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(typeLang::getName);
    }
}
