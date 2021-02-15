package ru.protei.portal.ui.common.client.widget.selector.contract;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class ContractFormSelector extends FormPopupSingleSelector<EntityOption> {

    @Inject
    public void init(ContractModel model, Lang lang) {
        setAsyncModel(model);
        setItemRenderer(value -> {
            if (value == null) {
                return defaultValue == null ? lang.selectValue() : defaultValue;
            }
            return lang.contractNum(value.getDisplayText());
        });
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    protected String defaultValue = null;
}
