package ru.protei.portal.ui.common.client.widget.selector.worktrigger;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_WorkTrigger;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public abstract class WorkTriggerModel implements Activity, SelectorModel<En_WorkTrigger> {

    @Override
    public En_WorkTrigger get(int elementIndex) {
        if (size(list) <= elementIndex) return null;
        return list.get(elementIndex);
    }

    private List<En_WorkTrigger> list = Arrays.asList(En_WorkTrigger.values());
}