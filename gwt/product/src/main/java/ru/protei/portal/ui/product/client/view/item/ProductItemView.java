package ru.protei.portal.ui.product.client.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.product.client.activity.item.AbstractProductItemView;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListActivity;

/**
 * Продукт
 */
public class ProductItemView extends Composite implements AbstractProductItemView {

    public ProductItemView() {
        initWidget (ourUiBinder.createAndBindUi (this));
    }

    public void setActivity(AbstractProductListActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName(String name) {
        this.name.setInnerText(name);
    }

    AbstractProductListActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);

    @UiField
    DivElement name;
//    @UiField
//    CheckBox active;
//    @UiField
//    Anchor menu;


    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductItemView> {}

}