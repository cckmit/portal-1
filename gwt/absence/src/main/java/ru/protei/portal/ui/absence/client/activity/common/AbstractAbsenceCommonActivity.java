package ru.protei.portal.ui.absence.client.activity.common;

import ru.protei.portal.core.model.dict.En_AbsenceReason;

public interface AbstractAbsenceCommonActivity {
    void onDateRangeChanged();
    default void onReasonChanged(En_AbsenceReason newReason){}
}
