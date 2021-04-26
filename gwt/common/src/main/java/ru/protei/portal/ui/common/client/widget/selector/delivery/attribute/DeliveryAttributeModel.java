package ru.protei.portal.ui.common.client.widget.selector.delivery.attribute;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public abstract class DeliveryAttributeModel implements Activity, SelectorModel<En_DeliveryAttribute> {

    @Override
    public En_DeliveryAttribute get(int elementIndex) {
        if (size(list) <= elementIndex) return null;
        return list.get(elementIndex);
    }

    private List<En_DeliveryAttribute> list = Arrays.asList(En_DeliveryAttribute.values());
}