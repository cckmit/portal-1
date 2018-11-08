package ru.protei.portal.app.portal.client.widget.navsearch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Поле поиска в навигационном баре
 */
public class NavSearchBox
        extends Composite implements HasValue< String > {

    public NavSearchBox() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @UiHandler("search")
    public void onSearchClicked(ClickEvent event) {
        event.preventDefault();

        boolean isOpened = root.getStyleName().contains( "open" );
        if ( isOpened ) {
            searchText.setText("");
            root.removeStyleName( "open" );
        } else {
            searchText.setFocus(true);
            root.addStyleName( "open" );
        }
    }

    @Override
    public String getValue() {
        return searchText.getValue();
    }

    @Override
    public void setValue( String value ) {
        searchText.setValue( value );
    }

    @Override
    public void setValue( String value, boolean fireEvents ) {
        searchText.setValue( value, fireEvents );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< String > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @UiField
    TextBox searchText;
    @UiField
    Anchor search;
    @UiField
    HTMLPanel root;

    interface NavSearchBoxUiBinder extends UiBinder< HTMLPanel, NavSearchBox > {}
    private static NavSearchBoxUiBinder ourUiBinder = GWT.create( NavSearchBoxUiBinder.class );
}