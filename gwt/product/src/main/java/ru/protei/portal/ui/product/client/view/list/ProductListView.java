package ru.protei.portal.ui.product.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListActivity;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListView;

/**
 * Вид списка продуктов
 */
public class ProductListView extends Composite implements AbstractProductListView {

    public ProductListView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setActivity(AbstractProductListActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getItemsContainer() {
        return productContainer;
    }

    @Override
    public String getParam() {
        return null;
    }

    @Override
    public HasValue<Boolean> isShowDepricated() {
        return showDepricated;
    }

    @UiHandler("showDepricated")
    public void onShowDepricatedClick (ClickEvent event)
    {
        if (showDepricated.getValue())
            showDepricated.addStyleName("active");
        else
            showDepricated.removeStyleName("checkbox");
        activity.onShowDepricatedClick();
    }

    @UiField
    HTMLPanel productContainer;
    @UiField
    ToggleButton showDepricated;

    AbstractProductListActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductListView > {}

}