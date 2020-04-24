package ru.protei.portal.ui.absence.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class AbsenceReasonButtonSelector extends ButtonPopupSingleSelector<En_AbsenceReason> {

    @Inject
    public void init(AbsenceReasonModel absenceReasonModel) {
        setModel(absenceReasonModel);
        setSearchEnabled(false);
        setItemRenderer(value -> value == null ? defaultValue : reasonLang.getName(value));
    }

    @Inject
    En_AbsenceReasonLang reasonLang;
}
