package ru.protei.portal.ui.common.client.widget.selector.item;

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
 * Вид одного элемента из выпадайки селектора
 */
public class SelectableItem
        extends Composite
        implements HasValue<Boolean>, HasEnabled
{
    public SelectableItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }


    public void setText(String text ) {
        this.text.setText( text );
        this.text.setTitle( text );
    }

    public void setInfo( String info ) {
        this.info.setText( info );
        this.info.setTitle( info );
    }

    public void setInfoVisible( boolean visible ) {
        this.info.setVisible( visible );
    }

    @Override
    public Boolean getValue() {
        return checkbox.getValue();
    }

    @Override
    public void setValue( Boolean value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( Boolean value, boolean fireEvents ) {
        checkbox.setValue( value, fireEvents );
        setSelectedStyle();

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< Boolean > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public boolean isEnabled() {
        return checkbox.isEnabled();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        checkbox.setEnabled( enabled );
    }

    public void setEnsureDebugId( String debugId ) {
        checkbox.ensureDebugId( debugId );
    }

    @UiHandler( "checkbox" )
    public void onCheckboxClicked(ClickEvent event) {
        ValueChangeEvent.fire( SelectableItem.this, checkbox.getValue() );
        setSelectedStyle();
    }

    @UiHandler( {"text", "info"} )
    public void onTextClicked( ClickEvent event ) {
        event.preventDefault();
        checkbox.setValue( !checkbox.getValue() );
        ValueChangeEvent.fire( SelectableItem.this, checkbox.getValue() );
        setSelectedStyle();
    }

    private void setSelectedStyle() {
        if ( checkbox.getValue() ) {
            text.addStyleName( "selected" );
            info.addStyleName( "selected" );
        } else {
            text.removeStyleName( "selected" );
            info.removeStyleName( "selected" );
        }
    }

    @UiField
    HTMLPanel panel;
    @UiField
    CheckBox checkbox;
    @UiField
    InlineLabel text;
    @UiField
    InlineLabel info;

    interface SelectorItemViewUiBinder extends UiBinder<HTMLPanel, SelectableItem > {}
    private static SelectorItemViewUiBinder ourUiBinder = GWT.create( SelectorItemViewUiBinder.class );
}