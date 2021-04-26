package ru.protei.portal.ui.common.client.widget.selector.delivery.status;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_DeliveryStatus;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public abstract class DeliveryStatusModel implements Activity, SelectorModel<En_DeliveryStatus> {

    @Override
    public En_DeliveryStatus get(int elementIndex) {
        if (size(list) <= elementIndex) return null;
        return list.get(elementIndex);
    }

    private List<En_DeliveryStatus> list = Arrays.asList(En_DeliveryStatus.values());
}