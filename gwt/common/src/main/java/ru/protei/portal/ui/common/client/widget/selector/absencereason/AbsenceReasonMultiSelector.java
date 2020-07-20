package ru.protei.portal.ui.common.client.widget.selector.absencereason;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.input.InputPopupMultiSelector;

public class AbsenceReasonMultiSelector extends InputPopupMultiSelector<En_AbsenceReason> {

    @Inject
    public void init(AbsenceReasonModel model, En_AbsenceReasonLang reasonLang, Lang lang) {
        setModel(model);
        setAddName(lang.buttonAdd());
        setClearName(lang.buttonClear());
        setItemRenderer(value -> reasonLang.getName(value));
    }
}
