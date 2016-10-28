package ru.protei.portal.ui.common.client.widget.selector.base;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовая логика селектора
 */
public abstract class Selector<T>
        extends Composite
        implements HasValue<T>,
        ClickHandler, ValueChangeHandler< String >,
        Window.ScrollHandler
{
    public void setValue( T value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( T value, boolean fireEvents ) {
        if ( value == null && !hasNullValue && !itemToNameModel.isEmpty() ) {
            value = itemToNameModel.entrySet().iterator().next().getKey();
            fireEvents = true;
        }

        if ( value == null || !itemToNameModel.containsKey( value ) ) {
            fillSelectorView( nullItemName );
        } else {
            fillSelectorView( itemToNameModel.get( value ) );
        }

        selectedOption = value;
        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    public T getValue() {
        return selectedOption;
    }

    public void refreshValue() {
        setValue( selectedOption );
    }

    public void setSearchEnabled( boolean isEnabled ) {
        this.searchEnabled = isEnabled;
    }

    public void setSearchAutoFocus( boolean isEnabled ) {
        this.searchAutoFocusEnabled = isEnabled;
    }

    public void setHasNullValue( boolean hasNullValue ) {
        this.hasNullValue = hasNullValue;
    }

    public void addOption( String name, T value ) {
        SelectorItem itemView = itemFactory.get();
        itemView.setName( name );
        itemView.addClickHandler( this );
        itemViewToModel.put( itemView, value );
        itemToViewModel.put( value, itemView );
        if ( value == null ) {
            nullItemName = name;
            nullItemView = itemView;
        }
        else {
            itemToNameModel.put( value, name );
        }

        popup.getChildContainer().add( itemView.asWidget() );
    }

    public void clearOptions() {
        popup.getChildContainer().clear();

        itemToViewModel.clear();
        itemToNameModel.clear();
        itemViewToModel.clear();

        nullItemName = null;
        nullItemView = null;
    }

    @Override
    public void onClick( ClickEvent event ) {
        T value = itemViewToModel.get( event.getSource() );
        if ( value == null && !itemViewToModel.containsKey( event.getSource() ) ) {
            return;
        }

        String name = value != null ? itemToNameModel.get( value ) : nullItemName;
        selectedOption = value;
        fillSelectorView( name );

        popup.hide();
        ValueChangeEvent.fire( this, value );
    }


    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< T > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }


    @Override
    public void onValueChange( ValueChangeEvent< String > event ) {
        String searchText = event.getValue().toLowerCase();

        boolean isEmptyResult = true;
        popup.getChildContainer().clear();

        if ( searchText.isEmpty() && nullItemView != null ) {
            popup.getChildContainer().add( nullItemView );
        }

        for ( Map.Entry< T, String > entry : itemToNameModel.entrySet() ) {
            if ( searchText.isEmpty() || entry.getValue().toLowerCase().contains(searchText) ) {
                SelectorItem itemView = itemToViewModel.get( entry.getKey() );
                if ( itemView != null ) {
                    popup.getChildContainer().add( itemView );
                }
                isEmptyResult = false;
            }
        }

        if ( isEmptyResult ) {
            addEmptyListGhostOption( lang.errNoMatchesFound() );
        }
    }

    @Override
    public void onWindowScroll( Window.ScrollEvent event ) {
        if ( popup.isAttached() ) {
            showPopup( relative );
        }
    }

    public abstract void fillSelectorView( String selectedValue );

    @Override
    protected void onLoad() {
        scrollRegistration = Window.addWindowScrollHandler( this );
    }

    @Override
    protected void onUnload() {
        scrollRegistration.removeHandler();
    }

    protected void showPopup( IsWidget relative ) {
        this.relative = relative;
        popup.setSearchVisible( searchEnabled );
        popup.setSearchAutoFocus( searchAutoFocusEnabled );

        popup.showNear( relative );
        popup.addValueChangeHandler( this );
        popup.clearSearchField();
    }

    private void addEmptyListGhostOption( String name ) {
        SelectorItem itemView = itemFactory.get();
        itemView.setName( name );
        itemView.addStyleName( "search-no-result" );
        popup.getChildContainer().add( itemView.asWidget() );
    }

    @Inject
    SelectorPopup popup;
    @Inject
    Lang lang;
    @Inject
    Provider<SelectorItem> itemFactory;

    protected String nullItemName = null;
    protected boolean hasNullValue = true;

    private boolean searchEnabled = false;
    private boolean searchAutoFocusEnabled = false;
    private IsWidget relative;
    private T selectedOption = null;
    private SelectorItem nullItemView;
    private HandlerRegistration scrollRegistration;

    private Map<SelectorItem, T> itemViewToModel = new HashMap< SelectorItem, T >();
    private Map<T, SelectorItem> itemToViewModel = new HashMap< T, SelectorItem >();
    private Map<T, String> itemToNameModel = new HashMap< T, String >();
}
