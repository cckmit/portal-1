package ru.protei.portal.ui.common.client.widget.selector.customertype;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorModel;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;

import java.util.Arrays;

public abstract class CustomerTypeModel implements Activity, SelectorModel<En_CustomerType> {

    @Override
    public void onSelectorLoad(SelectorWithModel<En_CustomerType> selector) {
        if (selector == null) {
            return;
        }
        if (selector.getValues() == null || selector.getValues().isEmpty()) {
            selector.fillOptions(Arrays.asList(En_CustomerType.values()));
        }
    }
}
