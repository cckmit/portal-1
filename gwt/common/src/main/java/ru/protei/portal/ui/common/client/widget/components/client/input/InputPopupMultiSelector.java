package ru.protei.portal.ui.common.client.widget.components.client.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.protei.portal.ui.common.client.widget.components.client.buttonselector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.SelectorItem;
import ru.protei.portal.ui.common.client.widget.components.client.selector.logic.multi.MultiValueSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectItemView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Cелектор c выпадающим списком, множественный выбор
 * (с интегрированной логикой)
 */
public class InputPopupMultiSelector<T> extends AbstractPopupSelector<T>
        implements HasValue<Set<T>>, HasEnabled, HasVisibility {

    public InputPopupMultiSelector() {
        initWidget( bsUiBinder.createAndBindUi( this ) );
    }

    public void setHeader( String label ) {
        this.label.removeStyleName( HIDE );
        this.label.getElement().setInnerText( label == null ? "" : label );
    }

    @Override
    public void setValue( Set<T> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( Set<T> value, boolean fireEvents ) {
        selector.setValue( value );
        showValue( value );
        if (fireEvents) {
            ValueChangeEvent.fire( this, value );
        }
    }

    @Override
    public Set<T> getValue() {
        return selector.getValue();
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<Set<T>> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled( boolean enabled ) {
        isEnabled = enabled;
        if (isEnabled) {
            itemContainer.removeStyleName( INACTIVE );
        } else {
            itemContainer.addStyleName( INACTIVE );
        }
        caretButton.setEnabled( isEnabled );
        clearButton.setEnabled( isEnabled );
        itemViews.forEach( ( v ) -> v.setEnabled( isEnabled ) );
    }

    @UiHandler({"caretButton"})
    public void onShowPopupClicked( ClickEvent event ) {
        if (!isEnabled) {
            return;
        }
        getPopup().getChildContainer().clear();
        getSelector().fillFromBegin( this );
        getPopup().showNear( itemContainer );
    }

    @UiHandler({"clearButton"})
    public void onClearClicked( ClickEvent event ) {
        if (!isEnabled) {
            return;
        }

        itemContainer.clear();
        itemViews.clear();
        getSelector().getSelectionModel().clear();
        clearButton.setVisible( false );

        ValueChangeEvent.fire( this, getValue() );
    }

    @Override
    protected void onSelectionChanged() {
        Set<T> value = getValue();
        showValue( value );
        getPopup().showNear( itemContainer );
        ValueChangeEvent.fire( this, value );
    }

    public void stopWatchForScrollOf( Widget widget ) {
    }

    public void watchForScrollOf( Widget widget ) {
    }

    public boolean isEmpty() {
        return getSelector().getSelectionModel().isEmpty();
    }

    protected void showValue( Set<T> values ) {
        itemContainer.clear();
        itemViews.clear();
        getValue().forEach( this::addItem );
        clearButton.setVisible( !isEmpty() );
    }

    protected SelectorItem makeSelectorItem( T element, String elementHtml ) {
        PopupSelectableItem item = new PopupSelectableItem();
        item.setElementHtml( elementHtml );
        item.setSelected( isSelected( element ) );
        return item;
    }

    @Override
    protected AbstractPageableSelector getSelector() {
        return selector;
    }

    protected MultiValueSelector<T> selector = new MultiValueSelector<T>();

    private void addItem( T item ) {
        SelectItemView itemView = itemViewProvider.get();
        itemView.setValue( getSelector().makeElementHtml( item ) );
        itemView.setEnabled( isEnabled );

        itemViews.add( itemView );
        itemView.setActivity( itemViewToRemove -> removeItem( itemViewToRemove, item ) );
        itemContainer.add( itemView );
    }

    private void removeItem( SelectItemView itemView, T item ) {
        itemContainer.remove( itemView );
        itemViews.remove( itemView );

        getSelector().getSelectionModel().select( item );
        ValueChangeEvent.fire( this, getValue() );
    }

    public void setAddEnsureDebugId( String debugId ) {
        caretButton.ensureDebugId( debugId );
    }

    public void setClearEnsureDebugId( String debugId ) {
        clearButton.ensureDebugId( debugId );
    }

    public void setItemContainerEnsureDebugId( String debugId ) {
        itemContainer.ensureDebugId( debugId );
    }

    public void setLabelEnsureDebugId( String debugId ) {
        label.ensureDebugId( debugId );
    }

    public void setAddName( String text ) {
        setAddName( null, text );
    }

    public void setAddName( String icon, String text ) {
        if (icon != null) {
            addIcon.setClassName( "fa " + icon );
        } else {
            addIcon.setClassName( "hide" );
        }
        add.setInnerText( text );
        add.removeClassName( "caret" );
    }

    public void setClearName( String text ) {
        setClearName( null, text );
    }

    public void setClearName( String icon, String text ) {
        if (icon != null) {
            clearIcon.setClassName( "fa " + icon );
        } else {
            clearIcon.setClassName( "hide" );
        }
        clear.setInnerText( text );
        clear.setClassName( "" );
    }

    public void setButtonStyle(String style) {
        if (style != null) {
            caretButton.removeStyleName("bg-white no-border");
            clearButton.removeStyleName("bg-white no-border");
            caretButton.addStyleName(style);
            clearButton.addStyleName(style);
        }
    }

    @UiField
    Button caretButton;
    @UiField
    HTMLPanel label;
    @UiField
    protected HTMLPanel itemContainer;
    @UiField
    SpanElement addIcon;
    @UiField
    SpanElement add;
    @UiField
    SpanElement clearIcon;
    @UiField
    SpanElement clear;
    @UiField
    Button clearButton;


    @Inject
    Provider<SelectItemView> itemViewProvider;

    List<SelectItemView> itemViews = new ArrayList<SelectItemView>();

    private boolean isEnabled = true;
    public static final String INACTIVE = "inactive";
    public static final String HIDE = "hide";

    interface BlockSelectorUiBinder extends UiBinder<HTMLPanel, InputPopupMultiSelector> {
    }

    private static BlockSelectorUiBinder bsUiBinder = GWT.create( BlockSelectorUiBinder.class );

}
