package ru.protei.portal.ui.common.client.widget.selector.item;

import com.google.gwt.core.client.GWT;
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
        checkbox.setFormValue( Boolean.FALSE.toString() );
    }

    public void setText( String text ) {
        checkbox.setText(text);
        checkbox.setTitle(text);
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
        checkbox.setFormValue( value.toString());
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

    @UiHandler("checkbox")
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        checkbox.setFormValue(event.getValue().toString());
        ValueChangeEvent.fire(this, event.getValue());
    }

    @UiField
    CheckBox checkbox;

    interface SelectorItemViewUiBinder extends UiBinder<HTMLPanel, SelectableItem > {}
    private static SelectorItemViewUiBinder ourUiBinder = GWT.create( SelectorItemViewUiBinder.class );
}