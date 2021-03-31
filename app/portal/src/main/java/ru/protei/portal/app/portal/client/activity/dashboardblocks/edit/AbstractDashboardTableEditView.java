package ru.protei.portal.app.portal.client.activity.dashboardblocks.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.FilterShortView;

public interface AbstractDashboardTableEditView extends IsWidget {

    void setActivity(AbstractDashboardTableEditActivity activity);

    void updateFilterSelector();

    HasValue<String> name();

    HasValue<FilterShortView> filter();

    HasVisibility filterCreateContainer();

    HasVisibility filterCreateNewIssues();

    HasVisibility filterCreateActiveIssues();
}
