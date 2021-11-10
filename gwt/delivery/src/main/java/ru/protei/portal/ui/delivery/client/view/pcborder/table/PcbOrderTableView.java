package ru.protei.portal.ui.delivery.client.view.pcborder.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.*;
import ru.protei.portal.ui.common.client.widget.table.GroupedTableWidget;
import ru.protei.portal.ui.delivery.client.activity.pcborder.table.AbstractPcbOrderTableActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.table.AbstractPcbOrderTableView;
import ru.protei.portal.ui.delivery.client.activity.pcborder.table.PcbOrderGroupType;
import ru.protei.portal.ui.delivery.client.view.pcborder.table.column.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
    public void addRecords(List<PcbOrder> pcbOrders, Comparator<Map.Entry<PcbOrderGroupType, List<PcbOrder>>> comparator) {
        table.addRecords(pcbOrders, comparator);
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

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    @Override
    public void updateRow(PcbOrder pcbOrder) {
        if(pcbOrder != null) {
            table.updateRow(pcbOrder);
        }
    }

    private void initTable() {

        editClickColumn.setDisplayPredicate(value -> policyService.hasPrivilegeFor(En_Privilege.PCB_ORDER_EDIT));
        removeClickColumn.setDisplayPredicate(value -> policyService.hasPrivilegeFor(En_Privilege.PCB_ORDER_REMOVE));

        NameColumn nameColumn = new NameColumn(lang);
        ModificationColumn modificationColumn = new ModificationColumn(lang);
        StateColumn stateColumn = new StateColumn(lang, stateLang);
        PromptnessColumn promptnessColumn = new PromptnessColumn(lang, promptnessLang);
        OrderTypeColumn orderTypeColumn = new OrderTypeColumn(lang, typeLang, stencilTypeLang);
        AmountColumn amountColumn = new AmountColumn(lang);
        OrderDateColumn orderDateColumn = new OrderDateColumn(lang);
        ReadyDateColumn readyDateColumn = new ReadyDateColumn(lang);
        ReceiptDateColumn receiptDateColumn = new ReceiptDateColumn(lang);
        ContractorColumn contractorColumn = new ContractorColumn(lang);

        columns.add(nameColumn);
        columns.add(modificationColumn);
        columns.add(stateColumn);
        columns.add(promptnessColumn);
        columns.add(orderTypeColumn);
        columns.add(amountColumn);
        columns.add(orderDateColumn);
        columns.add(readyDateColumn);
        columns.add(receiptDateColumn);
        columns.add(contractorColumn);

        table.addColumn(nameColumn.header, nameColumn.values);
        table.addColumn(modificationColumn.header, modificationColumn.values);
        table.addColumn(stateColumn.header, stateColumn.values);
        table.addColumn(promptnessColumn.header, promptnessColumn.values);
        table.addColumn(orderTypeColumn.header, orderTypeColumn.values);
        table.addColumn(amountColumn.header, amountColumn.values);
        table.addColumn(orderDateColumn.header, orderDateColumn.values);
        table.addColumn(readyDateColumn.header, readyDateColumn.values);
        table.addColumn(receiptDateColumn.header, receiptDateColumn.values);
        table.addColumn(contractorColumn.header, contractorColumn.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    @UiField
    Lang lang;
    @UiField
    GroupedTableWidget<PcbOrder, PcbOrderGroupType> table;
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
    @Inject
    En_PcbOrderStateLang stateLang;
    @Inject
    En_PcbOrderPromptnessLang promptnessLang;
    @Inject
    En_PcbOrderTypeLang typeLang;
    @Inject
    En_PcbOrderStencilTypeLang stencilTypeLang;

    AbstractPcbOrderTableActivity activity;
    EditClickColumn<PcbOrder> editClickColumn;
    RemoveClickColumn<PcbOrder> removeClickColumn;
    ClickColumnProvider<PcbOrder> columnProvider = new ClickColumnProvider<>();
    List<ClickColumn<PcbOrder>> columns = new ArrayList<>();

    private static PcbOrderTableViewUiBinder ourUiBinder = GWT.create(PcbOrderTableViewUiBinder.class);
    interface PcbOrderTableViewUiBinder extends UiBinder<HTMLPanel, PcbOrderTableView> {}
}
