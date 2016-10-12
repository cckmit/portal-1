package ru.protei.portal.ui.product.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListActivity;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListView;
import ru.protei.portal.ui.product.client.widgets.sortfieldselector.SortFieldSelector;

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
    public HasWidgets getItemsContainer() {
        return productContainer;
    }

    @Override
    public HasText getSearchPattern() { return search; }

    @Override
    public HasValue<Boolean> isShowDepricated() {
        return showDepricated;
    }

    @Override
    public HasValue<En_SortField> getSortField() { return sortFields; }

    @Override
    public HasValue<Boolean> getSortDir() { return sortDir; }

    @Override
    public void reset() {
        search.setText("");
        showDepricated.setValue(false);
        sortFields.setValue(En_SortField.prod_name);
        sortDir.setValue(true);
    }

    @UiHandler("showDepricated")
    public void onShowDepricatedClick (ClickEvent event)
    {
        if (showDepricated.getValue())
            showDepricated.removeStyleName("active");
        else
            showDepricated.addStyleName("active");
        activity.onShowDepricatedClick();
    }

    @UiHandler( "search" )
    public void onSearchFieldKeyUp (KeyUpEvent event)
    {
        changeTimer.cancel();
        changeTimer.schedule( 300 );
    }

    @UiHandler( "sortFields" )
    public void onSortFieldChanged( ValueChangeEvent<En_SortField> event ) {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

   @UiHandler( "sortDir" )
    public void onSortDirClicked ( ClickEvent event ) {

       if (sortDir.getValue())
           sortDir.removeStyleName("active");
       else
           sortDir.addStyleName("active");

       sortDir.setFocus(false);
       if ( activity != null ) {
           activity.onFilterChanged();
       }
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        changeTimer.cancel();
    }

    Timer changeTimer = new Timer() {
        @Override
        public void run() {
            changeTimer.cancel();
            if ( activity != null ) {
                activity.onFilterChanged();
            }
        }
    };

    @UiField
    TextBox search;
//    @UiField
//    Button searchButton;
    @UiField
    HTMLPanel productContainer;
    @UiField
    CheckBox showDepricated;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortFields;
    @UiField
    ToggleButton sortDir;

    AbstractProductListActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductListView > {}

}