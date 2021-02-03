package ru.protei.portal.ui.common.client.widget.selector.contract.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ContractType;
import ru.protei.portal.ui.common.client.lang.En_ContractTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ContractTypesMultiSelector extends InputPopupMultiSelector<En_ContractType> {

    @Inject
    public void init(Lang lang, En_ContractTypeLang typeLang) {
        setModel(elementIndex -> {
            try {
                return En_ContractType.values()[elementIndex];
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        });
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(typeLang::getName);
    }
}
