package ru.protei.portal.ui.delivery.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.deliveryfilter.DeliveryFilterWidget;
import ru.protei.portal.ui.common.client.widget.deliveryfilter.DeliveryFilterWidgetModel;
import ru.protei.portal.ui.delivery.client.activity.table.AbstractDeliveryTableActivity;
import ru.protei.portal.ui.delivery.client.activity.table.AbstractDeliveryTableView;
import ru.protei.portal.ui.delivery.client.view.table.column.ContactColumn;
import ru.protei.portal.ui.delivery.client.view.table.column.InfoColumn;
import ru.protei.portal.ui.delivery.client.view.table.column.ManagerColumn;
import ru.protei.portal.ui.delivery.client.view.table.column.NumberColumn;

import java.util.ArrayList;
import java.util.List;

public class DeliveryTableView extends Composite implements AbstractDeliveryTableView {

    @Inject
    public void init(DeliveryFilterWidgetModel deliveryFilterWidgetModel,
                     NumberColumn numberColumn,
                     InfoColumn infoColumn,
                     ContactColumn contactColumn,
                     ManagerColumn managerColumn) {
        this.numberColumn = numberColumn;
        this.infoColumn = infoColumn;
        this.contactColumn = contactColumn;
        this.managerColumn = managerColumn;
        this.filterWidget.onInit(deliveryFilterWidgetModel);
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractDeliveryTableActivity activity) {
        this.activity = activity;
        filterWidget.setOnFilterChangeCallback(activity::onFilterChanged);
        initTable();
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public DeliveryFilterWidget getFilterWidget() {
        return filterWidget;
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
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    @Override
    public void updateRow(Delivery item) {
        if(item != null)
            table.updateRow(item);
    }

    private void initTable() {

        columnProvider = new ClickColumnProvider<>();
        table.addColumn( numberColumn.header, numberColumn.values );
        table.addColumn( infoColumn.header, infoColumn.values );
        table.addColumn( contactColumn.header, contactColumn.values );
        table.addColumn( managerColumn.header, managerColumn.values );
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);

        editClickColumn.setActionHandler(activity);
        editClickColumn.setEditHandler(activity);
        removeClickColumn.setActionHandler(activity);
        removeClickColumn.setRemoveHandler(activity);

        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT));
        removeClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.DELIVERY_REMOVE));

        columns.add(numberColumn);
        columns.add(infoColumn);
        columns.add(contactColumn);
        columns.add(managerColumn);
        columns.add(editClickColumn);
        columns.add(removeClickColumn);

        columns.forEach(clickColumn -> {
            clickColumn.setHandler(activity);
            clickColumn.setColumnProvider(columnProvider);
        });

        table.setLoadHandler(activity);
    }

    @UiField
    Lang lang;
    @UiField
    InfiniteTableWidget<Delivery> table;
    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @Inject
    @UiField(provided = true)
    DeliveryFilterWidget filterWidget;
    @UiField
    HTMLPanel pagerContainer;

    NumberColumn numberColumn;
    InfoColumn infoColumn;
    ContactColumn contactColumn;
    ManagerColumn managerColumn;

    @Inject
    private EditClickColumn<Delivery> editClickColumn;
    @Inject
    RemoveClickColumn<Delivery> removeClickColumn;
    @Inject
    private PolicyService policyService;

    private ClickColumnProvider<Delivery> columnProvider = new ClickColumnProvider<>();
    private AbstractDeliveryTableActivity activity;

    List<ClickColumn<Delivery>> columns = new ArrayList<>();

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, DeliveryTableView> {}
}
