package ru.protei.portal.ui.common.client.widget.optionlist.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.common.ClickHTMLPanel;

/**
 * Один элемент списка чекбоксов
 */
public class OptionItem
        extends Composite
        implements HasValue<Boolean>, HasEnabled
{
    public OptionItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setName( String name ) {
        this.name.setText( name == null ? "" : name );
    }

    public void setInfo( String info ) {
        if (info == null) return;
        this.info.setVisible( true );
        this.info.setTitle( info );
        this.info.setText( info );
    }

    public void setText( String text ) {
        if (text == null) return;
        this.text.setVisible( true );
        this.text.setTitle( text );
        this.text.setText( text );
    }

    public void setNameBackgroundColor( String color ) {
        this.name.getElement().getStyle().setBackgroundColor( color );
    }

    @Override
    public Boolean getValue() {
        return checkbox.getValue();
    }

    @Override
    public void setValue( Boolean value ) {
        checkbox.setValue( value );
        checkbox.setFormValue( value.toString() );
    }

    @Override
    public void setValue( Boolean value, boolean fireEvents ) {
        checkbox.setValue( value, fireEvents );
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
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler< Boolean > handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @UiHandler( "checkbox" )
    public void onCheckboxClicked( ValueChangeEvent<Boolean> event ) {
        ValueChangeEvent.fire( this, event.getValue() );
        checkbox.setFormValue( event.getValue().toString());
    }

    @UiHandler( "panel" )
    public void onNameClicked( ClickEvent event ) {
        if ( checkbox.isEnabled() ) {
            checkbox.setValue( !checkbox.getValue(), true );
        }
    }

    public void setEnsureDebugId(String debugId) {
        checkbox.ensureDebugId(debugId);
    }

    @UiField
    CheckBox checkbox;
    @UiField
    ClickHTMLPanel panel;
    @UiField
    Label name;
    @UiField
    Label info;
    @UiField
    Label text;


    private static OptionItemUiBinder ourUiBinder = GWT.create( OptionItemUiBinder.class );
    interface OptionItemUiBinder extends UiBinder< HTMLPanel, OptionItem > {}

}