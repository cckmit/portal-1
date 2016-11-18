package ru.protei.portal.ui.issue.client.activity.filter;

import ru.protei.portal.core.model.query.CaseQuery;

/**
 * Абстракция активности фильтра обращений
 */
public interface AbstractIssueFilterActivity {
    void onFilterChanged();
}