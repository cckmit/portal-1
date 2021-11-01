package ru.protei.portal.ui.delivery.client.view.pcborder.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.table.GroupedTableWidget;
import ru.protei.portal.ui.delivery.client.activity.pcborder.table.AbstractPcbOrderTableActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.table.AbstractPcbOrderTableView;
import ru.protei.portal.ui.delivery.client.view.pcborder.table.column.OrderDateColumn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PcbOrderTableView extends Composite implements AbstractPcbOrderTableView {

    @Inject
    public void onInit(EditClickColumn<PcbOrder> editClickColumn,
                       RemoveClickColumn<PcbOrder> removeClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractPcbOrderTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        removeClickColumn.setHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setColumnProvider(columnProvider);

        columns.forEach(clickColumn -> {
            clickColumn.setHandler(activity);
            clickColumn.setColumnProvider(columnProvider);
        });

        table.setGroupFunctions(activity);
    }

    @Override
    public void addRecords(List<PcbOrder> roomReservations) {
        table.addRecords(roomReservations);
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public HasWidgets getFilterContainer() {
        return filterContainer;
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
        animation.setStyles("col-md-12", "col-md-9", "col-md-3", "col-md-3", "col-md-9");
    }

    private void initTable() {

        editClickColumn.setDisplayPredicate(value -> policyService.hasPrivilegeFor(En_Privilege.PCB_ORDER_EDIT));
        removeClickColumn.setDisplayPredicate(value -> policyService.hasPrivilegeFor(En_Privilege.PCB_ORDER_REMOVE));

        OrderDateColumn orderDateColumn = new OrderDateColumn(lang);
        table.addColumn(orderDateColumn.header, orderDateColumn.values);

        columns.add(orderDateColumn);

        table.addColumn(orderDateColumn.header, orderDateColumn.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    @UiField
    Lang lang;
    @UiField
    GroupedTableWidget<PcbOrder, En_PcbOrderState> table;
    @UiField
    HTMLPanel pagerContainer;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    PolicyService policyService;

    AbstractPcbOrderTableActivity activity;
    EditClickColumn<PcbOrder> editClickColumn;
    RemoveClickColumn<PcbOrder> removeClickColumn;
    ClickColumnProvider<PcbOrder> columnProvider = new ClickColumnProvider<>();
    List<ClickColumn<PcbOrder>> columns = new ArrayList<>();

    private static PcbOrderTableViewUiBinder ourUiBinder = GWT.create(PcbOrderTableViewUiBinder.class);
    interface PcbOrderTableViewUiBinder extends UiBinder<HTMLPanel, PcbOrderTableView> {}
}
