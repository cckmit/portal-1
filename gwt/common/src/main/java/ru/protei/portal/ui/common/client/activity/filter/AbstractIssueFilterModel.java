package ru.protei.portal.ui.common.client.activity.filter;

public interface AbstractIssueFilterModel {
    void onUserFilterChanged();
    void onPlanPresent(boolean isPresent);
}
