package ru.protei.portal.ui.common.client.activity.filter;

/**
 * Абстракция активности фильтра обращений
 */
public interface AbstractIssueFilterActivity {
    void onSaveFilterClicked();

    void onFilterRemoveClicked( Long id );

    void onOkSavingFilterClicked();

    void onCancelSavingFilterClicked();

    void onCreateFilterClicked();
}