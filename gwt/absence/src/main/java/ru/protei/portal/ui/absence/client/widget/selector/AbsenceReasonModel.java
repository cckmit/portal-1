package ru.protei.portal.ui.absence.client.widget.selector;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public abstract class AbsenceReasonModel implements Activity, SelectorModel<En_AbsenceReason> {

    @Override
    public En_AbsenceReason get(int elementIndex) {
        if (list == null) list = Arrays.asList(En_AbsenceReason.values());
        if (size(list) <= elementIndex) return null;
        return list.get(elementIndex);
    }

    private List<En_AbsenceReason> list;
}
