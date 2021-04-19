package ru.protei.portal.ui.absence.client.activity.common;

public interface AbstractAbsenceCommonActivity {
    void onDateRangeChanged();
    default void onReasonChangeToNightWork(){};
}
