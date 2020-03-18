package ru.protei.portal.ui.common.client.activity.filter;

/**
 * Абстракция активности фильтра обращений
 */
public interface AbstractIssueFilterActivity {
    void setModel(AbstractIssueFilterModel model);

    void onSaveFilterClicked();

    void onFilterRemoveClicked( Long id );

    void onOkSavingFilterClicked();

    void onCancelSavingFilterClicked();

    void onCreateFilterClicked();

    void onUserFilterChanged(Long id);
}