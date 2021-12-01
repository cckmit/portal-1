package ru.protei.portal.ui.plan.client.view.edit.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.columns.ActionIconClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.issuefilterselector.IssueFilterSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.plan.client.activity.edit.tables.AbstractUnplannedIssuesTableActivity;
import ru.protei.portal.ui.plan.client.activity.edit.tables.AbstractUnplannedIssuesTableView;
import ru.protei.portal.ui.plan.client.view.columns.IssueColumn;

import java.util.List;

public class UnplannedIssuesTableView extends Composite implements AbstractUnplannedIssuesTableView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setInputTextHandler();
    }

    @Override
    public void setActivity(AbstractUnplannedIssuesTableActivity activity) {
        this.activity = activity;
        initTable();
        issueNumber.setRegexp( CrmConstants.Masks.ONLY_DIGITS );
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
    public HasValue<FilterShortView> filter() {
        return filter;
    }

    @Override
    public HasValue<String> issueNumber() {
        return issueNumber;
    }

    @Override
    public HasValidable issueNumberValidator(){
        return issueNumber;
    }

    @Override
    public void setLimitLabel(String value) { this.limitLabel.setText( lang.planUnplannedTableLimit(value) ); }

    @Override
    public void setIssueDefaultCursor (boolean isDefault){
        issue.setStyleName(isDefault ? "cursor-default" : null);
    }

    @Override
    public ClickColumnProvider<CaseShortView> getIssuesColumnProvider() {
        return issuesColumnProvider;
    }

    @UiHandler("filter")
    public void onFilterChanged(ValueChangeEvent<FilterShortView> event) {
        if (activity != null) {
            activity.onFilterChanged(event.getValue());
        }
    }

    public void setInputTextHandler() {
        issueNumber.addInputHandler(event -> {
            startNumberChangedTimer();
        });
    }

    private void initTable() {

        issuesColumnProvider = new ClickColumnProvider<>();

        issue = new IssueColumn(lang);
        table.addColumn(issue.header, issue.values);
        issue.setHandler(value -> activity.onItemClicked(value));
        issue.setColumnProvider(issuesColumnProvider);

        ActionIconClickColumn<CaseShortView> assign = new ActionIconClickColumn<>("far fa-lg fa-caret-square-right", lang.planAddIssueToPlan(), null);
        table.addColumn(assign.header, assign.values);
        assign.setHandler(value -> {});
        assign.setActionHandler(value -> activity.onItemActionAssign(value));
        assign.setColumnProvider(issuesColumnProvider);
    }

    private void startNumberChangedTimer() {
        if (timer == null) {
            timer = new Timer() {
                @Override
                public void run() {
                    if (activity != null) {
                        activity.onIssueNumberChanged();
                    }
                }
            };
        } else {
            timer.cancel();
        }
        timer.schedule(300);
    }

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    IssueFilterSelector filter;
    @UiField
    ValidableTextBox issueNumber;
    @UiField
    TableWidget<CaseShortView> table;
    @UiField
    Label limitLabel;


    private IssueColumn issue;
    private Timer timer = null;
    private ClickColumnProvider<CaseShortView> issuesColumnProvider;
    private AbstractUnplannedIssuesTableActivity activity;


    interface UnplannedIssueTableViewBinder extends UiBinder<HTMLPanel, UnplannedIssuesTableView> {}
    private static UnplannedIssueTableViewBinder ourUiBinder = GWT.create(UnplannedIssueTableViewBinder.class);
}
