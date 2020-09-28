package ru.protei.portal.ui.common.client.view.selector;

import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.ui.common.client.selector.pageable.SelectorModel;

public class ElapsedTimeModel implements SelectorModel<En_TimeElapsedType> {
    @Override
    public En_TimeElapsedType get(int elementIndex) {
        if (elementIndex < En_TimeElapsedType.values().length) {
            return En_TimeElapsedType.values()[elementIndex];
        }

        return null;
    }
}
