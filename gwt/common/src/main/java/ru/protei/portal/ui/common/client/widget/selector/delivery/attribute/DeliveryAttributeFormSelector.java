package ru.protei.portal.ui.common.client.widget.selector.delivery.attribute;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.ui.common.client.lang.En_DeliveryAttributeLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class DeliveryAttributeFormSelector extends FormPopupSingleSelector<En_DeliveryAttribute> {

    @Inject
    public void init(DeliveryAttributeModel model, En_DeliveryAttributeLang lang) {
        setModel(model);
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
    }
}