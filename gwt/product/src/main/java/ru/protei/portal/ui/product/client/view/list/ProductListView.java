package ru.protei.portal.ui.product.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListView;
import ru.protei.portal.ui.product.client.activity.list.ProductListActivity;

/**
 * Вид списка продуктов
 */
public class ProductListView extends Composite implements AbstractProductListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setActivity(ProductListActivity activity) { this.activity = activity;  }

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

    @Override
    public void setListCreateBtnVisible( boolean isVisible ) {
        childContainer.setCreateButtonVisible( isVisible );
    }

    @UiHandler( "childContainer" )
    public void onAddClicked( AddEvent event ) {
        if ( activity != null ) {
            activity.onCreateClicked();
        }
    }

    @UiField
    PlateList childContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    ProductListActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductListView > {}

}