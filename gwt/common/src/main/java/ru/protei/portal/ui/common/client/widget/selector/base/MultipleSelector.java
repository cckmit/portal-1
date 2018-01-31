package ru.protei.portal.ui.common.client.widget.selector.base;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.widget.selector.event.SelectorChangeValEvent;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectableItem;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

import java.util.*;

/**
 * Базова логика селектора
 */
public abstract class MultipleSelector<T>
        extends Composite
        implements HasValue<Set<T>>, Window.ScrollHandler, ValueChangeHandler<Boolean>
{
    public void setValue( Set<T> values ) {
        setValue( values, false );
    }

    @Override
    public void setValue( Set<T> values, boolean fireEvents ) {
        selected = values == null ? new HashSet< T >() : values;
        if ( hasAnyValue ) {
            selectAnyValue( selected.isEmpty() );
        }

        for ( Map.Entry< SelectableItem, T > entry : itemViewToModel.entrySet() ) {
            entry.getKey().setValue( selected.contains( entry.getValue() ) );
        }

        getSelectedItemNamesAndFillSelectorView();
        if ( fireEvents ) {
            ValueChangeEvent.fire( this, values );
        }
    }

    public Set<T> getValue() {
        return selected;
    }

    public void setHasAnyValue( boolean hasAnyValue ) {
        this.hasAnyValue = hasAnyValue;
    }

    public void addOption( String name, T value ) {
        SelectableItem itemView;
        if ( value == null && hasAnyValue ) {
            itemView = makeAnySelectorItem( name );
        } else {
            itemView = itemFactory.get();
            itemView.setName( name );
            itemView.addValueChangeHandler( this );
            itemViewToModel.put( itemView, value );
            itemToViewModel.put( value, itemView );
            nameToItemView.put( name, itemView );
            itemToNameModel.put(value, name);
        }
        itemToDisplayOptionModel.put( value, new DisplayOption( name ) );
        popup.getChildContainer().add( itemView.asWidget() );
    }

    public void clearOptions() {
        popup.getChildContainer().clear();

        itemToNameModel.clear();
        itemViewToModel.clear();
        nameToItemView.clear();
        itemToDisplayOptionModel.clear();
    }


    @Override
    public void onValueChange( ValueChangeEvent< Boolean > event ) {
        T value = itemViewToModel.get( event.getSource() );

        if ( hasAnyValue ) {
            selectAnyValue( value == null );
        }

        if ( value != null ) {
            changeSelected(event.getValue(), value);
        }
        getSelectedItemNamesAndFillSelectorView();

        ValueChangeEvent.fire( this, selected );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< Set<T> > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public void onWindowScroll( Window.ScrollEvent event ) {
        if ( popup.isAttached() ) {
            showPopup( relative );
        }
    }

    public abstract void fillSelectorView( List<String> selectedValues );

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
        popup.showNear( relative );
        popup.setSearchVisible( true );
        popup.setSearchAutoFocus( true );
        popup.clearSearchField();
        popup.addValueChangeHandler( event -> {
            String searchText = event.getValue().toLowerCase();
            onSearchChanged( searchText );
        } );
    }

    protected void onSearchChanged( String searchText ) {
        boolean isEmptyResult = true;
        boolean exactMatch = false;

        popup.getChildContainer().clear();

//        if ( searchText.isEmpty() && nullItemView != null ) {
//            popup.getChildContainer().add( nullItemView );
//        }

        for ( Map.Entry< T, DisplayOption> entry : itemToDisplayOptionModel.entrySet() ) {
            String entryText = entry.getValue().getName().toLowerCase();
            if ( searchText.isEmpty() || entryText.contains(searchText) ) {
                SelectableItem itemView = itemToViewModel.get( entry.getKey() );
                if ( itemView != null ) {
                    popup.getChildContainer().add( itemView );
                }
                if(entryText.equals(searchText))
                    exactMatch = true;

                isEmptyResult = false;
            }
        }

//        if(exactMatch){
//            SelectorChangeValEvent.fire( this, null );
//        }else {
//            SelectorChangeValEvent.fire(this, event.getValue());
//        }
//
//        if ( isEmptyResult ) {
//            addEmptyListGhostOption( lang.errNoMatchesFound() );
//        }
    }

    protected void removeItemByName( String name ) {
        SelectableItem itemView = nameToItemView.get( name );
        if ( itemView == null ) {
            return;
        }

        itemView.setValue( false, true );
    }

    private void selectAnyValue( boolean isAnyValueSelected ) {
        anyItemView.setValue( isAnyValueSelected );
        if ( !isAnyValueSelected ) {
            return;
        }

        selected.clear();
        for ( Map.Entry< SelectableItem, T > entry : itemViewToModel.entrySet() ) {
            entry.getKey().setValue( !isAnyValueSelected );
        }
    }

    private void changeSelected( Boolean isSelected, T value) {
        if (isSelected) {
            selected.add(value);
        } else {
            selected.remove(value);
        }
    }

    private void getSelectedItemNamesAndFillSelectorView() {
        List<String> selectedNames = new ArrayList<>();
        if ( selected.isEmpty() && hasAnyValue ) {
            selectedNames.add( itemToNameModel.get( null ) );
        } else {
            for ( T item : selected ) {
                selectedNames.add( itemToNameModel.get( item ) );
            }
        }

        fillSelectorView( selectedNames );
    }

    protected void fillItemsSelection() {
        for (Map.Entry<SelectableItem, T> entry : itemViewToModel.entrySet()) {
            boolean isSelected = selected.contains(entry.getValue());
            entry.getKey().setValue(isSelected);
        }
    }

    private SelectableItem makeAnySelectorItem( String name ) {
        anyItemView = itemFactory.get();
        anyItemView.addStyleName( "multiple-any" );
        anyItemView.setName( name );
        anyItemView.addValueChangeHandler( this );
        itemToNameModel.put( null, name );

        return anyItemView;
    }

    @Inject
    SelectorPopup popup;
    @Inject
    Provider<SelectableItem> itemFactory;

    protected boolean hasAnyValue = false;
    private IsWidget relative;
    private Set<T> selected = new HashSet<>();
    private HandlerRegistration scrollRegistration;
    private SelectableItem anyItemView;

    private Map<T, String> itemToNameModel = new HashMap<T, String>();
    private Map<String, SelectableItem> nameToItemView = new HashMap<String, SelectableItem>();

    private Map<SelectableItem, T> itemViewToModel = new HashMap< SelectableItem, T >();

    protected Map<T, DisplayOption> itemToDisplayOptionModel = new HashMap<>();
    protected Map<T, SelectableItem> itemToViewModel = new HashMap<>();
}
