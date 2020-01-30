package ru.protei.portal.app.portal.client.view.dashboardblocks.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.edit.AbstractDashboardTableEditActivity;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.edit.AbstractDashboardTableEditView;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;

public class DashboardTableEditView extends Composite implements AbstractDashboardTableEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        filter.updateFilterType(En_CaseFilterType.CASE_OBJECTS);
    }

    @Override
    public void setActivity(AbstractDashboardTableEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<CaseFilterShortView> filter() {
        return filter;
    }

    @UiHandler("filter")
    public void onFilterChanged(ValueChangeEvent<CaseFilterShortView> event) {
        if (activity != null) {
            activity.onFilterChanged(event.getValue());
        }
    }

    @UiField
    TextBox name;
    @Inject
    @UiField(provided = true)
    IssueFilterSelector filter;

    private AbstractDashboardTableEditActivity activity;

    interface DashboardTableEditViewUiBinder extends UiBinder<Widget, DashboardTableEditView> {}
    private static DashboardTableEditViewUiBinder ourUiBinder = GWT.create(DashboardTableEditViewUiBinder.class);
}
