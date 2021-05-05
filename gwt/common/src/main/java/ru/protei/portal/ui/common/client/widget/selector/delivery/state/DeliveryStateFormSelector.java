package ru.protei.portal.ui.common.client.widget.selector.delivery.state;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryState;
import ru.protei.portal.ui.common.client.lang.En_DeliveryStateLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class DeliveryStateFormSelector extends FormPopupSingleSelector<En_DeliveryState> {

    @Inject
    public void init(DeliveryStateModel model, En_DeliveryStateLang lang) {
        setModel(model);
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
    }
}