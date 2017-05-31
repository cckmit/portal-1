package ru.protei.portal.ui.common.client.widget.togglebtn.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

/**
 * Вид кнопки-переключателя
 */
public class ToggleButton
        extends Composite
        implements HasValue<Boolean>, HasEnabled {

    public ToggleButton () {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setValue( Boolean value ) {
       setValue( value, false );
    }

    @Override
    public void setValue( Boolean value, boolean fireEvents ) {
        setActive( value );
        this.value.setValue( value );

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    public Boolean getValue() {
        return value.getValue();
    }

    public void setText( String value ) {
        this.text.setInnerText( value );
    }

    public void setTitle( String value ) {
        this.button.setTitle( value );
    }

    public void setIcon( String iconName ) {
        Element icon = DOM.createElement("i").cast();
        icon.setClassName( iconName );

        button.getElement().appendChild( icon );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<Boolean> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        reg = addDomHandler( event -> {
            boolean oldValue = getValue();
            boolean newValue = !oldValue;
            setValue( newValue );

            ValueChangeEvent.fireIfNotEqual( ToggleButton.this, oldValue, newValue );
        }, ClickEvent.getType() );
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        if ( reg != null ) {
            reg.removeHandler();
            reg = null;
        }
    }

    public void setImageSrc( String imageSrc ) {
        ImageElement img = DOM.createImg().cast();
        img.setSrc( imageSrc );

        button.getElement().appendChild( img );
    }

    @Override
    public boolean isEnabled() {
        return value.isEnabled();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        value.setEnabled( enabled );
        if ( enabled ) {
            button.removeStyleName( "disabled" );
        } else {
            button.addStyleName( "disabled" );
        }
    }

    private void setActive( Boolean value ) {
        if ( value ) {
            button.addStyleName( "active" );
        }
        else {
            button.removeStyleName( "active" );
        }
    }

    @UiField
    CheckBox value;
    @UiField
    SpanElement text;
    @UiField
    HTMLPanel button;

    HandlerRegistration reg;

    interface ToggleButtonUiBinder extends UiBinder<HTMLPanel, ToggleButton > {}
    private static ToggleButtonUiBinder ourUiBinder = GWT.create( ToggleButtonUiBinder.class );

}