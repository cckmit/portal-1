package ru.protei.portal.ui.common.client.widget.selector.delivery.status;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryStatus;
import ru.protei.portal.ui.common.client.lang.En_DeliveryStatusLang;
import ru.protei.portal.ui.common.client.widget.form.FormPopupSingleSelector;

public class DeliveryStatusFormSelector extends FormPopupSingleSelector<En_DeliveryStatus> {

    @Inject
    public void init(DeliveryStatusModel model, En_DeliveryStatusLang lang) {
        setModel(model);
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
    }
}