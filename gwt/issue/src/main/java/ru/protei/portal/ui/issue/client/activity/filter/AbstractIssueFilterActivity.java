package ru.protei.portal.ui.issue.client.activity.filter;

/**
 * Абстракция активности фильтра обращений
 */
public interface AbstractIssueFilterActivity {

    void onFilterCollapse();

    void onFilterRestore();

    void onSaveFilterClicked();

    void onFilterRemoveClicked( Long id );

    void onOkSavingClicked();

    void onCancelSavingClicked();
}