package ru.protei.portal.app.portal.client.activity.dashboardblocks.edit;

import ru.protei.portal.core.model.view.FilterShortView;

public interface AbstractDashboardTableEditActivity {

    void   onFilterChanged(FilterShortView filterShortView);

    void onCreateFilterNewIssuesClicked();

    void onCreateFilterActiveIssues();
}
