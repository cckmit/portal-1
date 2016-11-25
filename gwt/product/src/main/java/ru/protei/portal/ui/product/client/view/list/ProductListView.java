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
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.common.client.widget.platelist.events.AddEvent;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListActivity;
import ru.protei.portal.ui.product.client.activity.list.AbstractProductListView;

/**
 * Вид списка продуктов
 */
public class ProductListView extends Composite implements AbstractProductListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        search.getElement().setPropertyString( "placeholder", lang.search() );
    }

    public void setActivity(AbstractProductListActivity activity) { this.activity = activity;  }

    @Override
    public HasWidgets getItemsContainer() {
        return childContainer;
    }

    @Override
    public HasValue<String> searchPattern() { return search; }

    @Override
    public HasValue<Boolean> showDeprecated() {
        return showDeprecated;
    }

    @Override
    public HasValue<En_SortField> sortField() { return sortField; }

    @Override
    public HasValue<Boolean> sortDir() { return sortDir; }

    @UiHandler("showDeprecated")
    public void onShowDeprecatedClick(ClickEvent event)
    {
        if ( activity != null ) {
            activity.onFilterChanged();
        }
    }

    @UiHandler( "search" )
    public void onSearchFieldKeyUp (KeyUpEvent event)
    {
        changeTimer.cancel();
        changeTimer.schedule(300);
    }

    @UiHandler( "sortField" )
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

    @UiHandler( "childContainer" )
    public void onAddClicked( AddEvent event ) {
        if ( activity != null ) {
            activity.onCreateClicked();
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
    @UiField
    PlateList childContainer;
    @UiField
    CheckBox showDeprecated;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField
    Lang lang;

    AbstractProductListActivity activity;

    private static ProductViewUiBinder ourUiBinder = GWT.create (ProductViewUiBinder.class);
    interface ProductViewUiBinder extends UiBinder<HTMLPanel, ProductListView > {}

}