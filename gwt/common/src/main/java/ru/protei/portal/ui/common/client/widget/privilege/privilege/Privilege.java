package ru.protei.portal.ui.common.client.widget.privilege.privilege;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_Privilege;

/**
 * Виджет привилегии
 */
public class Privilege
        extends Composite
        implements HasValue<Boolean> {

    public Privilege() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setHeader( String value ) {
        this.header.setInnerText( value );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<Boolean> handler ) {
        return checkbox.addHandler( handler, ValueChangeEvent.getType() );
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
        checkbox.setValue( value );
        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    @UiField
    LabelElement header;
    @UiField
    CheckBox checkbox;

    interface PrivilegeListUiBinder extends UiBinder< HTMLPanel, Privilege> {}
    private static PrivilegeListUiBinder ourUiBinder = GWT.create( PrivilegeListUiBinder.class );
}
