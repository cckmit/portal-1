package ru.protei.portal.ui.common.client.activity.issuefilter;

public interface AbstractIssueFilterParamActivity {

    void onFilterChanged();

    void onUserFilterChanged();

    void onCompaniesFilterChanged();

    void onSaveFilterClicked();

    void onFilterRemoveClicked( Long id );

    void onOkSavingFilterClicked();

    void onCancelSavingFilterClicked();

    void onCreateFilterClicked();
}
