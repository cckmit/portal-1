package ru.protei.portal.ui.common.client.view.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.TimeElapsedTypeLang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class ElapsedTimeTypeMultiSelector extends InputPopupMultiSelector<En_TimeElapsedType> {
    @Inject
    public void init(ElapsedTimeModel model, Lang lang, TimeElapsedTypeLang timeElapsedTypeLang) {
        setModel(model);
        setClearName(lang.buttonClear());
        setAddName(lang.buttonAdd());
        setSearchEnabled(false);
        setItemRenderer(timeElapsedTypeLang::getName);
    }
}
