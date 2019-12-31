package ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.components.client.input.InputPopupMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.client.widget.selector.input.MultipleInputSelector;

import java.util.List;

public class PlatformMultiSelector extends InputPopupMultiSelector<PlatformOption>{

    @Inject
    public void init(PlatformModel model, Lang lang) {
        setAsyncSelectorModel(model);
        setAddName(lang.buttonAdd());
        setClearName( lang.buttonClear() );
        setSelectorItemRenderer( value -> value == null ? "" : value.getDisplayText() );
    }
}
