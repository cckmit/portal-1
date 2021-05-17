package ru.protei.portal.ui.common.client.widget.selector.delivery.type;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public abstract class DeliveryTypeModel implements Activity, SelectorModel<En_DeliveryType> {

    @Override
    public En_DeliveryType get(int elementIndex) {
        if (size(list) <= elementIndex) return null;
        return list.get(elementIndex);
    }

    private List<En_DeliveryType> list = Arrays.asList(En_DeliveryType.values());
}