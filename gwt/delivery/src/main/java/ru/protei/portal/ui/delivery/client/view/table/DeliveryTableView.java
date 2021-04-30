package ru.protei.portal.ui.delivery.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
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
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.delivery.client.activity.table.AbstractDeliveryTableActivity;
import ru.protei.portal.ui.delivery.client.activity.table.AbstractDeliveryTableView;

public class DeliveryTableView extends Composite implements AbstractDeliveryTableView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractDeliveryTableActivity activity) {
        this.activity = activity;
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
    public HTMLPanel getFilterContainer() {
        return filterContainer;
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    private void initTable() {

        columnProvider = new ClickColumnProvider<>();

        table.addColumn(columnName.header, columnName.values);
        columnName.setHandler(activity);
        columnName.setColumnProvider(columnProvider);

        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.DELIVERY_EDIT));
        table.addColumn(editClickColumn.header, editClickColumn.values);
        editClickColumn.setActionHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        table.setLoadHandler(activity);
    }

    private final ClickColumn<Delivery> columnName = new ClickColumn<Delivery>() {
//        protected String getColumnClassName() { return "contract-column-state"; }
        protected void fillColumnHeader(Element columnHeader) {}
        public void fillColumnValue(Element cell, Delivery delivery) {
            Element root = DOM.createDiv();
            root.setInnerHTML(delivery.getName());
            cell.appendChild(root);
        }
    };

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
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    private EditClickColumn<Delivery> editClickColumn;
    @Inject
    private PolicyService policyService;

    private ClickColumnProvider<Delivery> columnProvider = new ClickColumnProvider<>();
    private AbstractDeliveryTableActivity activity;

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, DeliveryTableView> {}
}
