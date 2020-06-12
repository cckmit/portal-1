package ru.protei.portal.ui.plan.client.view.edit.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ActionIconClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;
import ru.protei.portal.ui.plan.client.activity.edit.tables.AbstractUnplannedIssuesTableActivity;
import ru.protei.portal.ui.plan.client.activity.edit.tables.AbstractUnplannedIssuesTableView;
import ru.protei.portal.ui.plan.client.view.columns.IssueColumn;

import java.util.List;

public class UnplannedIssuesTableView extends Composite implements AbstractUnplannedIssuesTableView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractUnplannedIssuesTableActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void putRecords(List<CaseShortView> list) {
        list.forEach(table::addRow);
    }

    @Override
    public void setTotalRecords(int totalRecords) {
    }

    @Override
    public void updateFilterSelector() {
        filter.updateFilterType(En_CaseFilterType.CASE_OBJECTS);
    }

    @Override
    public HasValue<CaseFilterShortView> filter() {
        return filter;
    }

    @Override
    public void setLimitLabel(String value) { this.limitLabel.setText( lang.planUnplannedTableLimit(value) ); }

    private void initTable() {

        issuesColumnProvider = new ClickColumnProvider<>();

        IssueColumn number = new IssueColumn(lang);
        table.addColumn(number.header, number.values);
        number.setHandler(value -> activity.onItemClicked(value));
        number.setColumnProvider(issuesColumnProvider);

        ActionIconClickColumn<CaseShortView> assign = new ActionIconClickColumn<>("far fa-lg fa-caret-square-right", lang.planAddIssueToPlan(), null);
        table.addColumn(assign.header, assign.values);
        assign.setHandler(value -> {});
        assign.setActionHandler(value -> activity.onItemActionAssign(value));
        assign.setColumnProvider(issuesColumnProvider);
    }

    @UiHandler("filter")
    public void onFilterChanged(ValueChangeEvent<CaseFilterShortView> event) {
        if (activity != null) {
            activity.onFilterChanged(event.getValue());
        }
    }

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    IssueFilterSelector filter;
    @UiField
    TableWidget<CaseShortView> table;
    @UiField
    Label limitLabel;


    private ClickColumnProvider<CaseShortView> issuesColumnProvider;
    private AbstractUnplannedIssuesTableActivity activity;


    interface UnplannedIssueTableViewBinder extends UiBinder<HTMLPanel, UnplannedIssuesTableView> {}
    private static UnplannedIssueTableViewBinder ourUiBinder = GWT.create(UnplannedIssueTableViewBinder.class);
}