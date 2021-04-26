package ru.protei.portal.ui.common.client.widget.selector.delivery.type;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.ui.common.client.lang.En_DeliveryTypeLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class DeliveryTypeFormSelector extends FormPopupSingleSelector<En_DeliveryType> {

    @Inject
    public void init(DeliveryTypeModel model, En_DeliveryTypeLang lang) {
        setModel(model);
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
    }
}