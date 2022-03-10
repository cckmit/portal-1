package ru.protei.portal.app.portal.client.view.dashboardblocks.table.issue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardIssueTableActivity;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardIssueTableView;
import ru.protei.portal.app.portal.client.view.dashboardblocks.table.issue.columns.ContactColumn;
import ru.protei.portal.app.portal.client.view.dashboardblocks.table.issue.columns.ManagerColumn;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.issue.client.view.table.columns.InfoColumn;
import ru.protei.portal.ui.issue.client.view.table.columns.NumberColumn;

import java.util.List;
import java.util.function.Predicate;

public class DashboardIssueTableView extends Composite implements AbstractDashboardIssueTableView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        headerContainer.setDraggable(Element.DRAGGABLE_TRUE);
    }

    @Override
    public void setActivity(AbstractDashboardIssueTableActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearRows();
        count.setInnerText("");
    }

    @Override
    public void putRecords(List<CaseShortView> list) {
        list.forEach(table::addRow);
    }

    @Override
    public void setName(String name) {
        this.name.setInnerText(name);
    }

    @Override
    public void setCollapsed(boolean isCollapsed) {
        if (isCollapsed){
            tableContainer.addClassName("table-container-collapsed");
            collapseIcon.replaceClassName("fa-caret-down", "fa-caret-right");
            collapse.setTitle(lang.dashboardActionExpand());
        } else {
            tableContainer.removeClassName("table-container-collapsed");
            collapseIcon.replaceClassName("fa-caret-right", "fa-caret-down");
            collapse.setTitle(lang.dashboardActionCollapse());
        }
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        count.setInnerText("(" + totalRecords + ")");
    }

    @Override
    public void showLoader(boolean isShow) {
        loading.removeStyleName("d-block");
        if (isShow) {
            loading.addStyleName("d-block");
        }
    }

    @Override
    public void showTableOverflow(int showedRecords) {
        tableOverflow.setVisible(true);
        tableOverflowText.setInnerText(lang.dashboardTableOverflow(showedRecords));
    }

    @Override
    public void hideTableOverflow() {
        tableOverflow.setVisible(false);
    }

    @Override
    public void setEnsureDebugId(String debugId) {
        table.setEnsureDebugId(debugId);
    }

    @Override
    public void setChangeSelectionIfSelectedPredicate(Predicate<CaseShortView> changeSelectionIfSelectedPredicate) {
        columnProvider.setChangeSelectionIfSelectedPredicate(changeSelectionIfSelectedPredicate);
    }

    @Override
    public HandlerRegistration addDragStartHandler(DragStartHandler handler) {
        return addDomHandler(handler, DragStartEvent.getType());
    }

    @Override
    public HandlerRegistration addDragOverHandler(DragOverHandler handler) {
        return addDomHandler(handler, DragOverEvent.getType());
    }

    @Override
    public HandlerRegistration addDropHandler(DropHandler handler) {
        return addDomHandler(handler, DropEvent.getType());
    }

    @Override
    public HandlerRegistration addDragEndHandler(DragEndHandler handler) {
        return addDomHandler(handler, DragEndEvent.getType());
    }

    @UiHandler("open")
    public void onOpenClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onOpenClicked();
        }
    }

    @UiHandler("edit")
    public void onEditClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onEditClicked();
        }
    }

    @UiHandler("remove")
    public void onRemoveClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onRemoveClicked();
        }
    }

    @UiHandler("reload")
    public void onReloadClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onReloadClicked();
        }
    }

    @UiHandler("collapse")
    public void onCollapseClicked(ClickEvent event) {
        boolean isCollapsed = tableContainer.getClassName().contains("table-container-collapsed");

        activity.onCollapseClicked(!isCollapsed);
        setCollapsed(!isCollapsed);
    }

    private void initTable() {
        NumberColumn number = new NumberColumn(lang);
        table.addColumn(number.header, number.values);
        number.setHandler(activity);
        number.setColumnProvider(columnProvider);

        InfoColumn info = new InfoColumn(lang);
        table.addColumn(info.header, info.values);
        info.setHandler(activity);
        info.setColumnProvider(columnProvider);

        ContactColumn contact = new ContactColumn(lang);
        table.addColumn(contact.header, contact.values);
        contact.setHandler(activity);
        contact.setColumnProvider(columnProvider);

        ManagerColumn manager = new ManagerColumn(lang);
        table.addColumn(manager.header, manager.values);
        manager.setHandler(activity);
        manager.setColumnProvider(columnProvider);
    }

    @Inject
    @UiField
    Lang lang;
    @UiField
    SpanElement name;
    @UiField
    SpanElement count;
    @UiField
    Button open;
    @UiField
    Button edit;
    @UiField
    Button remove;
    @UiField
    Button collapse;
    @UiField
    Button reload;
    @UiField
    IndeterminateCircleLoading loading;
    @UiField
    TableWidget<CaseShortView> table;
    @UiField
    DivElement tableContainer;
    @UiField
    HTMLPanel tableOverflow;
    @UiField
    SpanElement tableOverflowText;
    @UiField
    Element collapseIcon;

    @UiField
    Element headerContainer;

    private AbstractDashboardIssueTableActivity activity;
    private ClickColumnProvider<CaseShortView> columnProvider = new ClickColumnProvider<>();

    interface CaseTableViewUiBinder extends UiBinder<HTMLPanel, DashboardIssueTableView> {}
    private static CaseTableViewUiBinder ourUiBinder = GWT.create(CaseTableViewUiBinder.class);
}
