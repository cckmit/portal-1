package ru.protei.portal.ui.common.client.widget.selector.report.timeelapsedgroup;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_TimeElapsedGroup;
import ru.protei.portal.ui.common.client.lang.En_TimeElapsedGroupLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class TimeElapsedGroupMultiSelector extends InputPopupMultiSelector<En_TimeElapsedGroup> {
    @Inject
    void init(TimeElapsedGroupModel model, En_TimeElapsedGroupLang enumLang, Lang lang) {
        setModel(model);

        setAddName(lang.buttonAdd());

        setSearchEnabled(false);
        setItemRenderer(enumLang::getName);
    }
}
