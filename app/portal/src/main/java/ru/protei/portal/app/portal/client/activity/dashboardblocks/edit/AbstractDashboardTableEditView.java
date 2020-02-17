package ru.protei.portal.app.portal.client.activity.dashboardblocks.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.CaseFilterShortView;

public interface AbstractDashboardTableEditView extends IsWidget {

    void setActivity(AbstractDashboardTableEditActivity activity);

    void updateFilterSelector();

    HasValue<String> name();

    HasValue<CaseFilterShortView> filter();

    HasVisibility filterCreateContainer();

    HasVisibility filterCreateNewIssues();

    HasVisibility filterCreateActiveIssues();
}
