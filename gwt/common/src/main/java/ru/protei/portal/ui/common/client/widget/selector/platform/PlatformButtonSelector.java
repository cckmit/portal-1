package ru.protei.portal.ui.common.client.widget.selector.platform;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class PlatformButtonSelector extends ButtonPopupSingleSelector<PlatformOption> {

    @Inject
    void init(PlatformModel model) {
        setAsyncModel(model);
        setHasNullValue(true);

        setItemRenderer(platformOption -> platformOption == null ? defaultValue : platformOption.getDisplayText());
    }
}
