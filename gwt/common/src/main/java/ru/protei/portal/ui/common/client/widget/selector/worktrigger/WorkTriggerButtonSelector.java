package ru.protei.portal.ui.common.client.widget.selector.worktrigger;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_WorkTrigger;
import ru.protei.portal.ui.common.client.lang.En_WorkTriggerLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class WorkTriggerButtonSelector extends ButtonPopupSingleSelector<En_WorkTrigger> {

    @Inject
    public void init(WorkTriggerModel model, En_WorkTriggerLang lang) {
        setModel(model);
        setSearchEnabled(false);
        setItemRenderer(value -> value == null ? defaultValue : lang.getName(value));
    }
}
