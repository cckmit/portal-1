package ru.protei.portal.ui.product.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;

/**
 * Created by frost on 10/4/16.
 */
public class ProductTableView {
    interface ProductTableViewUiBinder extends UiBinder<DivElement, ProductTableView> {
    }

    private static ProductTableViewUiBinder ourUiBinder = GWT.create(ProductTableViewUiBinder.class);

    public ProductTableView() {
        DivElement rootElement = ourUiBinder.createAndBindUi(this);
    }
}