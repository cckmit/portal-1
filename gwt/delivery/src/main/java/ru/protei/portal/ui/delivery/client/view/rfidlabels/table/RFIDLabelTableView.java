package ru.protei.portal.ui.delivery.client.view.rfidlabels.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.rfidlabels.table.AbstractRFIDLabelTableActivity;
import ru.protei.portal.ui.delivery.client.activity.rfidlabels.table.AbstractRFIDLabelTableView;
import ru.protei.portal.ui.delivery.client.view.rfidlabels.table.column.DeviceColumn;
import ru.protei.portal.ui.delivery.client.view.rfidlabels.table.column.EpcAndNameColumn;
import ru.protei.portal.ui.delivery.client.view.rfidlabels.table.column.InfoColumn;
import ru.protei.portal.ui.delivery.client.view.rfidlabels.table.column.LastScanDateColumn;


public class RFIDLabelTableView extends Composite implements AbstractRFIDLabelTableView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractRFIDLabelTableActivity activity) {
        this.activity = activity;
        initTable();
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public void triggerTableLoad() {
        table.setTotalRecords(table.getPageSize());
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        table.setTotalRecords(totalRecords);
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo(int page) {
        table.scrollToPage(page);
    }

    @Override
    public HasWidgets getFilterContainer() {
        return filterContainer;
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void updateRow(RFIDLabel item) {
        if(item != null) {
            table.updateRow(item);
        }
    }

    private void initTable() {
        EpcAndNameColumn epcAndName = new EpcAndNameColumn(lang);
        table.addColumn(epcAndName.header, epcAndName.values);

        InfoColumn info = new InfoColumn(lang);
        table.addColumn(info.header, info.values);

        LastScanDateColumn date = new LastScanDateColumn(lang);
        table.addColumn(date.header, date.values);

        DeviceColumn device = new DeviceColumn(lang);
        table.addColumn(device.header, device.values);

        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);

        editClickColumn.setEditHandler(activity);
        removeClickColumn.setRemoveHandler(activity);

        table.setPagerListener(activity);
        table.setLoadHandler(activity);
    }

    @UiField
    Lang lang;
    @UiField
    InfiniteTableWidget<RFIDLabel> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    EditClickColumn<RFIDLabel> editClickColumn;
    @Inject
    RemoveClickColumn<RFIDLabel> removeClickColumn;

    private AbstractRFIDLabelTableActivity activity;

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, RFIDLabelTableView> {}
}
