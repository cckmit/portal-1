package ru.protei.portal.ui.common.client.widget.selector.input;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
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
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.AbstractPopupSelector;
import ru.protei.portal.ui.common.client.selector.SelectorItem;
import ru.protei.portal.ui.common.client.selector.SelectorPopup;
import ru.protei.portal.ui.common.client.selector.pageable.AbstractPageableSelector;
import ru.protei.portal.ui.common.client.selector.pageable.MultiValuePageableSelector;
import ru.protei.portal.ui.common.client.widget.selector.item.PopupSelectableItem;
import ru.protei.portal.ui.common.client.widget.selector.item.SelectItemView;
import ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable.ArrowSelectableSelectorPopup;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.*;

/**
 * Cелектор c выпадающим списком, множественный выбор
 */
public class InputPopupMultiSelector<T> extends AbstractPopupSelector<T>
        implements HasValue<Set<T>>, HasEnabled, HasVisibility, HasValidable {

    public InputPopupMultiSelector() {
        initWidget( bsUiBinder.createAndBindUi( this ) );

        SelectorPopup popup = new ArrowSelectableSelectorPopup();
        setPopup(popup);
        setSearchEnabled(true);
        setAutoCloseable(false);

        setPageSize( CrmConstants.DEFAULT_SELECTOR_PAGE_SIZE );
        setEmptyListText( lang.emptySelectorList() );
        setEmptySearchText( lang.searchNoMatchesFound() );
        root.add(popup);
    }

    public void setHeader( String label ) {
        this.label.removeClassName( HIDE );
        this.label.setInnerText( label == null ? "" : label );
    }

    public void setMandatory( boolean mandatory ) {
        if ( mandatory ) {
            form.addClassName(REQUIRED);
        } else {
            form.removeClassName(REQUIRED);
        }
    }

    @Override
    public void setValue( Set<T> value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( Set<T> value, boolean fireEvents ) {
        selector.setValue( value );
        Set<T> selectorValue = selector.getValue();
        showValue(selectorValue);
        if (fireEvents) {
            ValueChangeEvent.fire(this, selectorValue);
        }

        validateSelector(isValidable);
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
            itemContainer.removeStyleName(INACTIVE);
        } else {
            itemContainer.addStyleName(INACTIVE);
        }
        caretButton.setEnabled( isEnabled );
        clearButton.setEnabled( isEnabled );
        itemViews.forEach( ( v ) -> v.setEnabled( isEnabled ) );
    }

    @Override
    public boolean isValid() {
        return isNotEmpty(getValue());
    }

    @Override
    public void setValid(boolean isValid) {
        if (isValid) {
            select2.removeClassName(HAS_ERROR);
        } else {
            select2.addClassName(HAS_ERROR);
        }
    }

    public void setValidation(boolean isValidable){
        this.isValidable = isValidable;
    }


    @UiHandler({"caretButton"})
    public void onShowPopupClicked( ClickEvent event ) {
        if (!isEnabled) {
            return;
        }

        if (!getPopup().isVisible()) {
            getPopup().getContainer().clear();
            getSelector().fillFromBegin( this );
            getPopup().showNear( select2 );
        }
    }

    @UiHandler({"clearButton"})
    public void onClearClicked( ClickEvent event ) {
        if (!isEnabled) {
            return;
        }

        itemContainer.clear();
        itemViews.clear();
        getSelector().getSelection().clear();
        clearButton.setVisible( false );
        validateSelector(isValidable);

        ValueChangeEvent.fire( this, getValue() );
    }

    @Override
    protected void onSelectionChanged() {
        Set<T> value = getValue();
        showValue( value );
        validateSelector(isValidable);

        ValueChangeEvent.fire( this, value );
    }

    public void setNullItem(Supplier<T> selectorNullItem) {
        getSelector().setNullItem(selectorNullItem);
    }

    public boolean isEmpty() {
        return getSelector().getSelection().isEmpty();
    }

    protected void showValue( Set<T> values ) {
        itemContainer.clear();
        itemViews.clear();
        getValue().forEach( this::addItem );
        clearButton.setVisible( !isEmpty() );
    }

    @Override
    protected SelectorItem<T> makeSelectorItem( T element, String elementHtml ) {
        PopupSelectableItem<T> item = new PopupSelectableItem<>();
        item.setElementHtml( elementHtml );
        item.setSelected( isSelected( element ) );
        item.setTitle(elementHtml);
        return item;
    }

    @Override
    protected AbstractPageableSelector<T> getSelector() {
        return selector;
    }

    protected MultiValuePageableSelector<T> selector = new MultiValuePageableSelector<T>();

    private void addItem( T item ) {
        SelectItemView itemView = itemViewProvider.get();
        itemView.setValue( getSelector().makeElementHtml( item ) );
        itemView.setTitle( getSelector().makeElementName( item ) );
        itemView.setEnabled( isEnabled );

        itemViews.add( itemView );
        itemView.setActivity( itemViewToRemove -> removeItem( itemViewToRemove, item ) );
        itemContainer.add( itemView );

        validateSelector(isValidable);
    }

    private void removeItem( SelectItemView itemView, T item ) {
        itemContainer.remove( itemView );
        itemViews.remove( itemView );
        getSelector().getSelection().select( item );
        clearButton.setVisible( !isEmpty() );
        validateSelector(isValidable);

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
        label.setId( DebugIds.DEBUG_ID_PREFIX +  debugId );
    }

    public void setAddName( String text ) {
        setAddName( null, text );
    }

    public void setAddName( String icon, String text ) {
        if (icon != null) {
            addIcon.setClassName( "fa " + icon );
        } else {
            addIcon.setClassName( HIDE );
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
            clearIcon.setClassName( HIDE );
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

    private void validateSelector(boolean isValidable) {
        if (isValidable) {
            setValid(isValid());
        }
    }

    @UiField
    HTMLPanel root;
    @UiField
    Button caretButton;
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
    @UiField
    Lang lang;
    @UiField
    LabelElement label;
    @UiField
    DivElement form;
    @UiField
    protected DivElement select2;

    @Inject
    Provider<SelectItemView> itemViewProvider;

    List<SelectItemView> itemViews = new ArrayList<>();

    private boolean isEnabled = true;
    private boolean isValidable;

    interface BlockSelectorUiBinder extends UiBinder<HTMLPanel, InputPopupMultiSelector> {}
    private static BlockSelectorUiBinder bsUiBinder = GWT.create( BlockSelectorUiBinder.class );
}
