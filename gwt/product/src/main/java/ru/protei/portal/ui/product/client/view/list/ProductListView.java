package ru.protei.portal.ui.product.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.common.client.widget.platelist.events.AddEvent;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListActivity;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListView;

/**
 * Вид списка продуктов
 */
public class ProductListView extends Composite implements AbstractProductListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setActivity(AbstractProductListActivity activity) { this.activity = activity;  }

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

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

    AbstractProductListActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductListView > {}

}