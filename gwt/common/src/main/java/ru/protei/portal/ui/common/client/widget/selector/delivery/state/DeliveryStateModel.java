package ru.protei.portal.ui.common.client.widget.selector.delivery.state;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_DeliveryState;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public abstract class DeliveryStateModel implements Activity, SelectorModel<En_DeliveryState> {

    @Override
    public En_DeliveryState get(int elementIndex) {
        if (size(list) <= elementIndex) return null;
        return list.get(elementIndex);
    }

    private List<En_DeliveryState> list = Arrays.asList(En_DeliveryState.values());
}