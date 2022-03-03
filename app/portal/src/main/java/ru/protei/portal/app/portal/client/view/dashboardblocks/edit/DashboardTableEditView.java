package ru.protei.portal.app.portal.client.view.dashboardblocks.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.edit.AbstractDashboardTableEditActivity;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.edit.AbstractDashboardTableEditView;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;
import ru.protei.portal.ui.common.client.widget.selector.project.filter.ProjectFilterSelector;

public class DashboardTableEditView extends Composite implements AbstractDashboardTableEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        hideIssueFilter();
        hideProjectFilter();
    }

    @Override
    public void setActivity(AbstractDashboardTableEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void updateIssueFilterSelector() {
        issueFilter.updateFilterType(En_CaseFilterType.CASE_OBJECTS);
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<FilterShortView> issueFilter() {
        return issueFilter;
    }

    @Override
    public HasValue<FilterShortView> projectFilter() {
        return projectFilter;
    }

    @Override
    public void showIssueFilter() {
        issueFilter.setVisible(true);
        issueFilterLabel.setVisible(true);
        filterCreateContainer.setVisible(true);
    }

    @Override
    public void showProjectFilter() {
        projectFilter.setVisible(true);
        projectFilterLabel.setVisible(true);
    }

    @Override
    public void hideIssueFilter() {
        issueFilter.setVisible(false);
        issueFilterLabel.setVisible(false);
        filterCreateContainer.setVisible(false);
    }

    @Override
    public void hideProjectFilter() {
        projectFilter.setVisible(false);
        projectFilterLabel.setVisible(false);
    }

    @Override
    public HasVisibility filterCreateContainer() {
        return filterCreateContainer;
    }

    @Override
    public HasVisibility filterCreateNewIssues() {
        return createFilterNewIssues;
    }

    @Override
    public HasVisibility filterCreateActiveIssues() {
        return createFilterActiveIssues;
    }

    @UiHandler("issueFilter")
    public void onIssueFilterChanged(ValueChangeEvent<FilterShortView> event) {
        if (activity != null) {
            activity.onFilterChanged(event.getValue());
        }
    }

    @UiHandler("projectFilter")
    public void onProjectFilterChanged(ValueChangeEvent<FilterShortView> event) {
        if (activity != null) {
            activity.onFilterChanged(event.getValue());
        }
    }

    @UiHandler("createFilterNewIssues")
    public void createFilterNewIssuesClick(ClickEvent event) {
        if (activity != null) {
            activity.onCreateFilterNewIssuesClicked();
        }
    }

    @UiHandler("createFilterActiveIssues")
    public void createFilterActiveIssuesClick(ClickEvent event) {
        if (activity != null) {
            activity.onCreateFilterActiveIssues();
        }
    }

    @UiField
    TextBox name;
    @UiField
    Label issueFilterLabel;
    @Inject
    @UiField(provided = true)
    IssueFilterSelector issueFilter;
    @UiField
    Label projectFilterLabel;
    @Inject
    @UiField(provided = true)
    ProjectFilterSelector projectFilter;
    @UiField
    HTMLPanel filterCreateContainer;
    @UiField
    Button createFilterNewIssues;
    @UiField
    Button createFilterActiveIssues;

    private AbstractDashboardTableEditActivity activity;

    interface DashboardTableEditViewUiBinder extends UiBinder<Widget, DashboardTableEditView> {}
    private static DashboardTableEditViewUiBinder ourUiBinder = GWT.create(DashboardTableEditViewUiBinder.class);
}
