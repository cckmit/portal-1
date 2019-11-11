package ru.protei.portal.ui.common.client.widget.selector.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectorItem;
import ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector.SelectorItemHandler;

/**
 * Вид одного элемента из выпадайки селектора
 */
public class PopupSelectableItem<T>
        extends Composite
        implements  HasEnabled, SelectorItem<T>
{
    public PopupSelectableItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T t) {
        value = t;
    }

    public void setText( String text ) {
        this.text.setText( text );
        this.text.setTitle( text );
    }

    public void setInfo( String info ) {
        if (info != null) {
            this.info.setVisible( true );
            this.info.setText( info );
            this.info.setTitle( info );
        }
    }

    @Override
    public void setElementHtml(String name ) {
        text.getElement().setInnerHTML( name );
    }

    public void setSelected( Boolean isSelected ) {
        checkbox.setValue( isSelected );
        setSelectedStyle();
    }

    @Override
    public boolean isEnabled() {
        return checkbox.isEnabled();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        checkbox.setEnabled( enabled );
    }

    @Override
    public void addSelectorHandler(SelectorItemHandler selectorItemHandler) {
        this.selectorItemHandler = selectorItemHandler;
    }

    @UiHandler( "checkbox" )
    public void onCheckboxClicked(ClickEvent event) {
        selectorItemHandler.onSelectorItemClicked(this);
        setSelectedStyle();
    }

    @UiHandler( {"text", "info"} )
    public void onTextClicked( ClickEvent event ) {
        event.preventDefault();
        checkbox.setValue( !checkbox.getValue() );
        if(selectorItemHandler!=null) {
            selectorItemHandler.onSelectorItemClicked(this);
        }
    }

    public void setEnsureDebugId( String debugId ) {
        checkbox.ensureDebugId( debugId );
    }

    private void setSelectedStyle() {
        if ( checkbox.getValue() ) {
            text.addStyleName( SELECTED );
            info.addStyleName( SELECTED );
        } else {
            text.removeStyleName( SELECTED );
            info.removeStyleName( SELECTED );
        }
    }

    @Override
    public void setFocus( boolean isFocused ) {
        checkbox.setFocus( isFocused );
    }

    @Override
    public HandlerRegistration addKeyUpHandler( KeyUpHandler keyUpHandler) {
        return addHandler( keyUpHandler, KeyUpEvent.getType() );
    }

    @UiHandler("checkbox")
    public void onKeyUpEvent( KeyUpEvent keyUpEvent) {
        keyUpEvent.preventDefault();

        KeyUpEvent.fireNativeEvent(keyUpEvent.getNativeEvent(), this);
    }

    @Override
    public void setElementWidget( Widget widget ) {
        panel.clear();
        panel.add(widget);
    }

    private SelectorItemHandler selectorItemHandler;
    private T value;

    @UiField
    HTMLPanel panel;
    @UiField
    CheckBox checkbox;
    @UiField
    InlineLabel text;
    @UiField
    InlineLabel info;

    public static final String SELECTED = "selected";

    interface SelectorItemViewUiBinder extends UiBinder<HTMLPanel, PopupSelectableItem> {}
    private static SelectorItemViewUiBinder ourUiBinder = GWT.create( SelectorItemViewUiBinder.class );
}