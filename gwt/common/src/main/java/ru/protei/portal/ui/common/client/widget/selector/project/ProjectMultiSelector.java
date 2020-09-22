package ru.protei.portal.ui.common.client.widget.selector.project;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ProjectMultiSelector extends InputPopupMultiSelector<EntityOption> {
    @Inject
    public void init(AsyncProjectModel model, Lang lang) {
        setAsyncSearchModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(EntityOption::getDisplayText);
    }
}
