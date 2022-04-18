package ru.protei.portal.app.portal.client.activity.dashboardblocks.table;

import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;

public interface AbstractDashboardProjectTableActivity extends ClickColumn.Handler<Project> {

    void onItemClicked(Project project);

    void onOpenClicked();

    void onEditClicked();

    void onRemoveClicked();

    void onCollapseClicked(boolean isCollapsed);

    void onReloadClicked();
}
