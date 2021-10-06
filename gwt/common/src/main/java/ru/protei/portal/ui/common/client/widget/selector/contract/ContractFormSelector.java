package ru.protei.portal.ui.common.client.widget.selector.contract;

import com.google.inject.Inject;
import ru.protei.portal.core.model.struct.ContractInfo;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class ContractFormSelector extends FormPopupSingleSelector<ContractInfo> {

    @Inject
    public void init(ContractModel model, Lang lang) {
        setAsyncModel(model);
        setItemRenderer(value -> {
            if (value == null) {
                return defaultValue == null ? lang.selectValue() : defaultValue;
            }
            return lang.contractNum(value.getNumber());
        });
    }
}
