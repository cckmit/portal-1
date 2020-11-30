package ru.protei.portal.ui.common.client.widget.selector.worktrigger;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_WorkTrigger;
import ru.protei.portal.ui.common.client.lang.En_WorkTriggerLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class WorkTriggerButtonMultiSelector extends InputPopupMultiSelector<En_WorkTrigger> {

    @Inject
    public void init(WorkTriggerModel model, En_WorkTriggerLang enWorkTriggerLang, Lang lang) {
        setModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(value -> enWorkTriggerLang.getName(value));
    }
}
