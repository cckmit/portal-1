package ru.protei.portal.ui.sitefolder.client.view.server.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ServerMultiSelector extends InputPopupMultiSelector<EntityOption> {
    @Inject
    public void init(AsyncServerModel model, Lang lang) {
        setAsyncSearchModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(EntityOption::getDisplayText);
    }
}
