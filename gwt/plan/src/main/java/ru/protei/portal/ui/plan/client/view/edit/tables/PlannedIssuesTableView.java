package ru.protei.portal.ui.plan.client.view.edit.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ActionIconClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.plan.client.activity.edit.tables.AbstractPlannedIssuesTableActivity;
import ru.protei.portal.ui.plan.client.activity.edit.tables.AbstractPlannedIssuesTableView;
import ru.protei.portal.ui.plan.client.view.columns.DragColumn;
import ru.protei.portal.ui.plan.client.view.columns.IssueColumn;

import java.util.List;

public class PlannedIssuesTableView extends Composite implements AbstractPlannedIssuesTableView {

    @Inject
    public void onInit(RemoveClickColumn<CaseShortView> removeClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.removeClickColumn = removeClickColumn;
    }

    @Override
    public void setActivity(AbstractPlannedIssuesTableActivity activity) {
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
    public void moveColumnVisibility (boolean isVisible){
        moveToAnotherPlanColumn.setColumnVisibility(isVisible);
    }

    @Override
    public ClickColumnProvider<CaseShortView> getIssuesColumnProvider() {
        return issuesColumnProvider;
    }

    private void initTable() {
        table.setDraggableRows(true);
        dragColumn.setHandler( activity );
        table.addColumn(dragColumn.header, dragColumn.values);

        issuesColumnProvider = new ClickColumnProvider<>();

        IssueColumn number = new IssueColumn(lang);
        table.addColumn(number.header, number.values);
        number.setHandler(value -> activity.onItemClicked(value));
        number.setColumnProvider(issuesColumnProvider);

        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setHandler(value -> activity.onItemClicked(value) );
        removeClickColumn.setColumnProvider(issuesColumnProvider);

        table.addColumn(removeClickColumn.header, removeClickColumn.values);

        ActionIconClickColumn<CaseShortView> assign = new ActionIconClickColumn<>("far fa-lg fa-caret-square-right", lang.planMoveIssueToAnotherPlan(), null);
        moveToAnotherPlanColumn = table.addColumn(assign.header, assign.values);
        assign.setHandler(value -> {});
        assign.setActionHandler(new ClickColumn.Handler<CaseShortView>() {
            public void onItemClicked(CaseShortView value) {}
            public void onItemClicked(CaseShortView value, Element target) {
                activity.onItemActionAssign(value, new PlannedIssuesTableView.CustomUIObject(target));
            }
        });
        assign.setColumnProvider(issuesColumnProvider);
    }


    private static class CustomUIObject extends UIObject {
        CustomUIObject(Element element) {
            setElement(element);
        }
    }

    @Inject
    @UiField
    Lang lang;

    @UiField
    TableWidget<CaseShortView> table;

    private ClickColumnProvider<CaseShortView> issuesColumnProvider;
    private RemoveClickColumn<CaseShortView> removeClickColumn;
    DragColumn< CaseShortView > dragColumn = new DragColumn<>();
    private AbstractPlannedIssuesTableActivity activity;
    private AbstractColumn moveToAnotherPlanColumn;


    interface PlannedIssueTableViewBinder extends UiBinder<HTMLPanel, PlannedIssuesTableView> {}
    private static PlannedIssueTableViewBinder ourUiBinder = GWT.create(PlannedIssueTableViewBinder.class);
}