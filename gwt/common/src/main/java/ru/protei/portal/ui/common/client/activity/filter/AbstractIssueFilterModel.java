package ru.protei.portal.ui.common.client.activity.filter;

import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

public interface AbstractIssueFilterModel {
    void onUserFilterChanged();
    void onPlanPresent(boolean isPresent);
}
