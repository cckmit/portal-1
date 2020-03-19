package ru.protei.portal.ui.common.client.activity.filter;

import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;

/**
 * Абстракция активности фильтра обращений
 */
public interface AbstractIssueFilterActivity {
    void setView(AbstractIssueFilterView view, AbstractIssueFilterWidgetView paramView);

    void onSaveFilterClicked();

    void onFilterRemoveClicked( Long id );

    void onOkSavingFilterClicked();

    void onCancelSavingFilterClicked();

    void onCreateFilterClicked();

    void onUserFilterChanged(Long id);
}