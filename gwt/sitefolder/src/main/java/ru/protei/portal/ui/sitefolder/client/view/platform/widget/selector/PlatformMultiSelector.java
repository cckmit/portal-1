package ru.protei.portal.ui.sitefolder.client.view.platform.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.input.InputPopupMultiSelector;

public class PlatformMultiSelector extends InputPopupMultiSelector<PlatformOption>{

    @Inject
    public void init(PlatformModel model, Lang lang) {
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName( lang.buttonClear() );
        setItemRenderer( value -> value == null ? "" : value.getDisplayText() );
    }
}
