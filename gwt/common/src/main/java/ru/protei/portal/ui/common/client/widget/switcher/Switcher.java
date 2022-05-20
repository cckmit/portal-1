package ru.protei.portal.ui.common.client.widget.switcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.winter.web.common.client.common.DisplayStyle;

/**
 * Вид элемента – свитчера
 */
public class Switcher
        extends Composite
        implements HasValue<Boolean>, HasEnabled
{

    public Switcher() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setValue( Boolean value, boolean fireEvents ) {
        check.setChecked(value);

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    @Override
    public void setValue( Boolean value ) {
        setValue( value, false );
    }

    public void setHeader( String value ) {
        label.setInnerText(value);
    }

    @Override
    public Boolean getValue() {
        return check.isChecked();
    }

    @Override
    public void setEnabled( boolean value ) {
        check.setDisabled(!value);
    }

    @Override
    public boolean isEnabled() {
        return !check.isDisabled();
    }

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<Boolean> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    protected void onAttach() {
        super.onAttach();

        reg = addDomHandler( event -> {
            if ( isEnabled() ) {
                boolean oldValue = getValue();
                boolean newValue = !oldValue;
                setValue( newValue );

                ValueChangeEvent.fireIfNotEqual( Switcher.this, oldValue, newValue );
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

    @UiField
    InputElement check;
    @UiField
    LabelElement label;

    HandlerRegistration reg;

    interface SwitcherViewUiBinder extends UiBinder<HTMLPanel, Switcher> {}
    private static SwitcherViewUiBinder ourUiBinder = GWT.create( SwitcherViewUiBinder.class );

}