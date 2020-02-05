package ru.protei.portal.app.portal.client.activity.dashboardblocks.edit;

import ru.protei.portal.core.model.view.CaseFilterShortView;

public interface AbstractDashboardTableEditActivity {

    void onFilterChanged(CaseFilterShortView filterShortView);

    void onCreateFilterNewIssuesClicked();

    void onCreateFilterActiveIssues();
}
