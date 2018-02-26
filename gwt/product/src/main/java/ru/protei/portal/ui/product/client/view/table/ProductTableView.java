package ru.protei.portal.ui.product.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.DynamicColumn;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductTableActivity;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductTableView;

public class ProductTableView extends Composite implements AbstractProductTableView{

    @Inject
    public void onInit(EditClickColumn<DevUnit> editClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractProductTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler( activity );
        editClickColumn.setEditHandler( activity );
        editClickColumn.setColumnProvider( columnProvider );

        name.setHandler( activity );
        name.setColumnProvider( columnProvider );
        table.setLoadHandler( activity );
        table.setPagerListener( activity );
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers( tableContainer, previewContainer, filterContainer );
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
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
    public void setProductsCount(Long issuesCount) {
        table.setTotalRecords( issuesCount.intValue() );
    }

    @Override
    public int getPageSize() {
        return table.getPageSize();
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo(int page) {
        table.scrollToPage( page );
    }

    @Override
    public void updateRow(DevUnit item) {
        if(item != null)
            table.updateRow(item);
    }

    private void initTable () {
        editClickColumn.setPrivilege( En_Privilege.COMPANY_EDIT );
        name = new DynamicColumn<>(lang.productName(), "product-name", DevUnit::getName);

        table.addColumn( name.header, name.values );
        table.addColumn( editClickColumn.header, editClickColumn.values );
    }

    @UiField
    InfiniteTableWidget<DevUnit> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    Lang lang;


    ClickColumnProvider< DevUnit > columnProvider = new ClickColumnProvider<>();
    EditClickColumn< DevUnit > editClickColumn;
    DynamicColumn<DevUnit> name;

    AbstractProductTableActivity activity;

    private static ProductTableViewUiBinder ourUiBinder = GWT.create(ProductTableViewUiBinder.class);
    interface ProductTableViewUiBinder extends UiBinder<HTMLPanel, ProductTableView> {}
}