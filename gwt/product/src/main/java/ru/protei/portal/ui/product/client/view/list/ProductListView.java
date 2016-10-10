package ru.protei.portal.ui.product.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
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
    public String getSearchPattern() {
        return search.getText();
    }

    @Override
    public HasValue<Boolean> isShowDepricated() {
        return showDepricated;
    }

    @Override
    public String getSortField() {
        //return sortFields.getSelectedValue();
        return null;
    }

    @Override
    public HasValue<Boolean> getSortDir() {
        return sortDir;
    }

    @Override
    public void reset() {
        showDepricated.setValue(false);
        search.setText("");
    }

    @UiHandler("showDepricated")
    public void onShowDepricatedClick (ClickEvent event)
    {
        if (showDepricated.getValue())
            showDepricated.addStyleName("active");
        else
            showDepricated.removeStyleName("active");
        activity.onShowDepricatedClick();
    }

    @UiHandler( "searchButton" )
    public void onSearchClicked( ClickEvent event ) {
        if ( activity != null ) {
            activity.onSearchClicked();
        }
    }

    @UiHandler( "search" )
    public void onSearchFieldKeyPress (KeyPressEvent event)
    {
        if ( activity != null ) {
            activity.onSearchFieldKeyPress();
        }
    }

    @UiHandler( "search" )
    public void onSearchFieldKeyUp (KeyUpEvent event)
    {
        if ( activity != null ) {
            activity.onSearchFieldKeyPress();
        }
    }


    @UiHandler( "search" )
    public void onSearchFieldChanged (ValueChangeEvent<String> event)
    {
        if ( activity != null ) {
            activity.onSearchFieldKeyPress();
        }
    }

    @UiHandler( "sortDir" )
    public void onSortDirClicked ( ClickEvent event ) {
//        if ( activity != null ) {
//            activity.onSearchFieldKeyPress();
//        }
    }

    @UiField
    TextBox search;
    @UiField
    Button searchButton;
    @UiField
    HTMLPanel productContainer;
    @UiField
    CheckBox showDepricated;
    //@UiField
    //ListBox sortFields;
    @UiField
    ToggleButton sortDir;

    AbstractProductListActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductListView > {}

}