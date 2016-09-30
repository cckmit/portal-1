package ru.protei.portal.ui.product.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListActivity;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListView;

/**
 *  Список продуктов
 */
public class ProductListView extends Composite implements AbstractProductListView {

    public ProductListView() {
        initWidget (ourUiBinder.createAndBindUi (this));
    }

    public void setActivity(AbstractProductListActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getItemsContainer() {
        return productContainer;
    }


    @UiField
    HTMLPanel productContainer;

    AbstractProductListActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductListView > {}

}