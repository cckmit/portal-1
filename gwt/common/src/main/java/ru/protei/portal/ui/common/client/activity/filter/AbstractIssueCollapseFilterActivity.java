package ru.protei.portal.ui.common.client.activity.filter;

/**
 * Абстракция активности фильтра обращений
 */
public interface AbstractIssueCollapseFilterActivity {
    void onFilterCollapse();

    void onFilterRestore();
}