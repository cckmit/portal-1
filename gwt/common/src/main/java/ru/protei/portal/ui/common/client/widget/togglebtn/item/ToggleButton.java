package ru.protei.portal.ui.common.client.widget.togglebtn.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Вид кнопки-переключателя
 */
public class ToggleButton
        extends Composite
        implements HasValue<Boolean> {

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

    public void setFacetEnabled( boolean enable ) {
        this.facetEnabled = enable;
    }

    public void setText( String value ) {
        this.text.setInnerText( value );
    }

    public void setCaption( String value ) {
        this.caption.setInnerText( value );
    }

    public void setTitle( String value ) {
        this.root.setTitle( value );
    }

    public void setIcon( String iconName ) {
        this.icon.setClassName( iconName );
    }

    public void setButtonStyle( String style ) {
        this.button.setStyleName( style );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<Boolean> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        reg = addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                boolean oldValue = getValue();
                boolean newValue = !oldValue;
                setValue( newValue );

                ValueChangeEvent.fireIfNotEqual( ToggleButton.this, oldValue, newValue );
            }
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

    private void setActive( Boolean value ) {
        if ( value ) {
            button.addStyleName( facetEnabled ? "btn-down" : "active" );
        }
        else {
            button.removeStyleName( facetEnabled ? "btn-up" : "active" );
        }
    }

    @UiField
    CheckBox value;
    @UiField
    SpanElement text;
    @UiField
    Element icon;
    @UiField
    SpanElement caption;
    @UiField
    HTMLPanel button;
    @UiField
    HTMLPanel root;

    boolean facetEnabled = false;
    HandlerRegistration reg;

    interface ToggleButtonUiBinder extends UiBinder<HTMLPanel, ToggleButton > {}
    private static ToggleButtonUiBinder ourUiBinder = GWT.create( ToggleButtonUiBinder.class );

}