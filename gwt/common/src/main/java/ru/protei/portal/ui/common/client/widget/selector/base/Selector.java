package ru.protei.portal.ui.common.client.widget.selector.base;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.event.HasSelectorChangeValHandlers;
import ru.protei.portal.ui.common.client.widget.selector.event.SelectorChangeValEvent;
import ru.protei.portal.ui.common.client.widget.selector.event.SelectorChangeValHandler;
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
        Window.ScrollHandler,
        HasSelectorChangeValHandlers
{
    public void setValue( T value ) {
        setValue( value, false );
    }

//    public void findAndSelectValue (Predicate<T> predicate, boolean fireEvents) {
//        setValue(itemToDisplayOptionModel.keySet().stream().filter(predicate).findFirst().orElse(null), fireEvents);
//    }

    @Override
    public void setValue( T value, boolean fireEvents ) {
        if ( value == null && !hasNullValue && !itemToDisplayOptionModel.isEmpty() ) {
            value = itemToDisplayOptionModel.entrySet().iterator().next().getKey();
            fireEvents = true;
        }

        selectedOption = value;
        if ( value == null || !itemToDisplayOptionModel.containsKey( value ) ) {
            fillSelectorView( nullItemOption );
        } else {
            fillSelectorView( itemToDisplayOptionModel.get( value ) );
        }

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
        itemViewToModel.put(itemView, value);
        itemToViewModel.put(value, itemView);
        if ( value == null ) {
            nullItemOption = new DisplayOption( name );
            nullItemView = itemView;
        }
        else {
            itemToDisplayOptionModel.put( value, new DisplayOption( name ) );
        }

        popup.getChildContainer().add(itemView.asWidget());
    }

    public void addOption( DisplayOption option, T value ) {
        SelectorItem itemView = itemFactory.get();
        itemView.setName( option.getName() );
        itemView.setStyle( option.getStyle() );
        itemView.setIcon( option.getIcon() );
        itemView.addClickHandler( this );
        itemViewToModel.put(itemView, value);
        itemToViewModel.put(value, itemView);
        if ( value == null ) {
            nullItemOption = option;
            nullItemView = itemView;
        }
        else {

            itemToDisplayOptionModel.put( value, option );
        }

        popup.getChildContainer().add(itemView.asWidget());
    }

    public void clearOptions() {
        popup.getChildContainer().clear();

        itemToViewModel.clear();
        itemToDisplayOptionModel.clear();
        itemViewToModel.clear();

        nullItemOption = null;
        nullItemView = null;
    }

    @Override
    public void onClick( ClickEvent event ) {
        T value = itemViewToModel.get(event.getSource());
        if ( value == null && !itemViewToModel.containsKey( event.getSource() ) ) {
            return;
        }

        DisplayOption option = value != null ? itemToDisplayOptionModel.get( value ) : nullItemOption;
        selectedOption = value;
        fillSelectorView( option );

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
        boolean exactMatch = false;

        popup.getChildContainer().clear();

        if ( searchText.isEmpty() && nullItemView != null ) {
            popup.getChildContainer().add( nullItemView );
        }

        for ( Map.Entry< T, DisplayOption> entry : itemToDisplayOptionModel.entrySet() ) {
            String entryText = entry.getValue().getName().toLowerCase();
            if ( searchText.isEmpty() || entryText.contains(searchText) ) {
                SelectorItem itemView = itemToViewModel.get( entry.getKey() );
                if ( itemView != null ) {
                    popup.getChildContainer().add( itemView );
                }
                if(entryText.equals(searchText))
                    exactMatch = true;

                isEmptyResult = false;
            }
        }

        if(exactMatch){
            SelectorChangeValEvent.fire(this, null);
        }else {
            SelectorChangeValEvent.fire(this, event.getValue());
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

    @Override
    public HandlerRegistration addSelectorChangeValHandler(SelectorChangeValHandler handler) {
        return addHandler( handler, SelectorChangeValEvent.getType() );
    }

    public abstract void fillSelectorView( DisplayOption selectedValue );

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
        popup.setSearchAutoFocus(searchAutoFocusEnabled);

        popup.showNear(relative);
        popup.addValueChangeHandler( this );
        popup.clearSearchField();
    }

    protected void closePopup(){
        popup.hide();
    }

    private void addEmptyListGhostOption( String name ) {
        SelectorItem itemView = itemFactory.get();
        itemView.setName( name );
        itemView.addStyleName( "search-no-result" );
        popup.getChildContainer().add( itemView.asWidget() );
    }

    public void addCloseHandler(CloseHandler<PopupPanel> handler){
        popup.addCloseHandler(handler);
    }

    @Inject
    SelectorPopup popup;
    @Inject
    Lang lang;
    @Inject
    Provider<SelectorItem> itemFactory;

    protected DisplayOption nullItemOption;
    protected boolean hasNullValue = true;

    private boolean searchEnabled = false;
    private boolean searchAutoFocusEnabled = false;
    private IsWidget relative;
    private T selectedOption = null;
    private SelectorItem nullItemView;
    private HandlerRegistration scrollRegistration;

    protected Map<SelectorItem, T> itemViewToModel = new HashMap<>();
    protected Map<T, SelectorItem> itemToViewModel = new HashMap<>();
    protected Map<T, DisplayOption> itemToDisplayOptionModel = new HashMap<>();
}
