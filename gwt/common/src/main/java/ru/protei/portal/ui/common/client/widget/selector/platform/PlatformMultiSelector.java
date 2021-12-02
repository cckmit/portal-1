package ru.protei.portal.ui.common.client.widget.selector.platform;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class PlatformMultiSelector extends InputPopupMultiSelector<PlatformOption>{

    @Inject
    public void init(PlatformModel model, Lang lang) {
        setAsyncModel(model);
        setAddName(lang.buttonAdd());
        setClearName( lang.buttonClear() );
        setItemRenderer( value -> value == null ? "" : sanitizeHtml(value.getDisplayText()) );
    }
}
