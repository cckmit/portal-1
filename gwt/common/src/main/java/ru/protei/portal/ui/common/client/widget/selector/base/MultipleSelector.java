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
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectableItem;
import ru.protei.portal.ui.common.client.widget.selector.popup.SelectorPopup;

import java.util.*;

/**
 * Базова логика селектора
 */
public abstract class MultipleSelector<T>
        extends Composite
        implements HasValue<Set<T>>, Window.ScrollHandler, ValueChangeHandler<Boolean>,
        SelectorWithModel<T>
{

    protected abstract void onUserCanAddMoreItems(boolean isCanAdd);

    public void setValue( Set<T> values ) {
        setValue( values, false );
    }

    public Collection<T> getValues() {
        return itemToDisplayOptionModel.keySet();
    }

    public void setSelectorModel( SelectorModel<T> selectorModel ) {
        this.selectorModel = selectorModel;
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
        addOption(name, null, value);
    }

    public void addOption( String name, String info, T value ) {
        SelectableItem itemView;
        if ( value == null && hasAnyValue ) {
            itemView = makeAnySelectorItem( name );
        } else {
            itemView = itemFactory.get();
            itemView.setText( name );
            if (info == null) {
                itemView.setInfoVisible(false);
                itemView.setInfo( "" );
            } else {
                itemView.setInfoVisible(true);
                itemView.setInfo( info );
            }
            itemView.addValueChangeHandler( this );
            itemViewToModel.put( itemView, value );
            itemToViewModel.put( value, itemView );
            itemToNameModel.put(value, name);
        }
        itemToDisplayOptionModel.put( value, new DisplayOption( name ) );
        popup.getChildContainer().add( itemView.asWidget() );
    }

    public void clearOptions() {
        popup.getChildContainer().clear();

        itemToNameModel.clear();
        itemViewToModel.clear();
        itemToDisplayOptionModel.clear();
    }

    public void hidePopup() {
        popup.hide();
    }

    public void setSelectedLimit(int selectedLimit) {
        this.selectedLimit = selectedLimit;
        onUserCanAddMoreItems(!isLimitSet() || isLimitNotReached());
    }

    @Override
    public void onValueChange( ValueChangeEvent< Boolean > event ) {
        T value = itemViewToModel.get( event.getSource() );

        if (selectedLimit > 0 && event.getValue() && selected.size() >= selectedLimit) {
            SelectableItem item = itemToViewModel.get(value);
            if (item != null) {
                item.setValue(false, true);
            }
            return;
        }

        if ( hasAnyValue ) {
            selectAnyValue( value == null );
        }

        if ( value != null ) {
            changeSelected(event.getValue(), value);
        }
        getSelectedItemNamesAndFillSelectorView();

        if (isLimitSet()) {
            onUserCanAddMoreItems(isLimitNotReached());
        }

        ValueChangeEvent.fire( this, selected );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< Set<T> > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public void onWindowScroll( Window.ScrollEvent event ) {
        if ( popup.isAttached() ) {
            popup.showNear(relative);
        }
    }

    public abstract void fillSelectorView( List<T> selectedItems );

    @Override
    protected void onLoad() {
        scrollRegistration = Window.addWindowScrollHandler( this );
        if ( selectorModel != null ) {
            selectorModel.onSelectorLoad(this);
        }
    }

    @Override
    protected void onUnload() {
        scrollRegistration.removeHandler();
        if ( selectorModel != null ) {
            selectorModel.onSelectorUnload(this);
        }
    }

    protected void showPopup( IsWidget relative ) {
        this.relative = relative;
        popup.showNear( relative );
        popup.setSearchVisible( true );
        popup.setSearchAutoFocus( true );
        popup.clearSearchField();
        onSearchChanged( "" );
        if (popupValueChangeHandlerRegistration != null) {
            popupValueChangeHandlerRegistration.removeHandler();
        }
        popupValueChangeHandlerRegistration = popup.addValueChangeHandler( event -> {
            String searchText = event.getValue().toLowerCase();
            onSearchChanged( searchText );
        } );
    }

    protected void onSearchChanged( String searchText ) {
        popup.getChildContainer().clear();

        for ( Map.Entry< T, DisplayOption> entry : itemToDisplayOptionModel.entrySet() ) {
            String entryText = entry.getValue().getName().toLowerCase();
            if ( searchText.isEmpty() || entryText.contains(searchText) ) {
                SelectableItem itemView = itemToViewModel.get( entry.getKey() );
                if ( itemView != null ) {
                    popup.getChildContainer().add( itemView );
                }
            }
        }
    }

    protected void setItemChecked(T item, boolean isChecked) {
        SelectableItem selectableItem = itemToViewModel.get(item);
        if (selectableItem == null) {
            return;
        }

        selectableItem.setValue(isChecked, true);
    }

    protected void clearSelected() {
        selected.clear();
        for ( Map.Entry< SelectableItem, T > entry : itemViewToModel.entrySet() ) {
            entry.getKey().setValue( false );
        }
    }


    @Override
    public void refreshValue() {
        getSelectedItemNamesAndFillSelectorView();
    }

    protected void reselectValuesIfNeeded() {
        Set<T> value = getValue();
        if (CollectionUtils.isNotEmpty(value)) {
            setValue(value, false);
        }
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
        List<T> selectedItems = new ArrayList<>();
        if ( selected.isEmpty() && hasAnyValue ) {
            selectedItems.add( null );
        } else {
            selectedItems.addAll(selected);
        }
        fillSelectorView(selectedItems);
    }

    protected String getNameForItem(T item) {
        return itemToNameModel.get(item);
    }

    protected void fillItemsSelection() {
        for (Map.Entry<SelectableItem, T> entry : itemViewToModel.entrySet()) {
            boolean isSelected = selected.contains(entry.getValue());
            entry.getKey().setValue(isSelected);
        }
    }

    private SelectableItem makeAnySelectorItem( String name ) {
        anyItemView = itemFactory.get();
        anyItemView.addStyleName( UiConstants.Styles.MULTIPLE_ANY );
        anyItemView.setText( name );
        anyItemView.addValueChangeHandler( this );
        itemToNameModel.put( null, name );

        return anyItemView;
    }

    private boolean isLimitSet() {
        return selectedLimit > 0;
    }

    private boolean isLimitNotReached() {
        return selected.size() < selectedLimit;
    }

    @Inject
    protected SelectorPopup popup;
    @Inject
    protected Provider<SelectableItem> itemFactory;

    private int selectedLimit = 0;
    protected boolean hasAnyValue = false;
    private IsWidget relative;
    private Set<T> selected = new HashSet<>();
    private HandlerRegistration scrollRegistration;
    private SelectableItem anyItemView;
    private HandlerRegistration popupValueChangeHandlerRegistration;

    private SelectorModel<T> selectorModel;

    protected Map<T, String> itemToNameModel = new HashMap<T, String>();

    private Map<SelectableItem, T> itemViewToModel = new HashMap< SelectableItem, T >();

    protected Map<T, DisplayOption> itemToDisplayOptionModel = new HashMap<>();
    protected Map<T, SelectableItem> itemToViewModel = new HashMap<>();
}
