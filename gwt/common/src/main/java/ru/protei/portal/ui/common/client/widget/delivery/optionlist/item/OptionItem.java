package ru.protei.portal.ui.common.client.widget.delivery.optionlist.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.events.EditEvent;
import ru.protei.portal.ui.common.client.events.EditHandler;
import ru.protei.portal.ui.common.client.events.HasEditHandlers;
import ru.protei.portal.ui.common.client.events.clone.CloneEvent;
import ru.protei.portal.ui.common.client.events.clone.CloneHandler;
import ru.protei.portal.ui.common.client.events.clone.HasCloneHandlers;

/**
 * Однострочный кликабельный элемент с статусом, номером, именем, количеством подэлементов и возможностью выделения
 */
public class OptionItem
        extends Composite
        implements HasValue<Boolean>, HasEnabled, HasEditHandlers
{
    public OptionItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initClickHandlers();
    }

    public void setNumber( String number ) {
        if (number == null) return;
        this.number.setInnerText( number );
    }

    public void setStatusTitle( String status ) {
        if (status == null) return;
        this.status.setTitle( status );
    }

    public void setName( String name ) {
        if (name == null) return;
        this.name.setInnerText( name );
    }

    public void setAmount( Integer amount ) {
        if (amount == null) return;
        this.amount.setInnerText( String.valueOf(amount) );
    }

    public void setStatusColor( String color ) {
        this.status.getStyle().setColor( color );
    }

    public void setItemEditable( boolean isItemEditable ) {
        //TODO
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

    @Override
    public HandlerRegistration addEditHandler(EditHandler handler) {
        return addHandler(handler, EditEvent.getType());
    }

    @UiHandler( "checkbox" )
    public void onCheckboxClicked( ValueChangeEvent<Boolean> event ) {
        ValueChangeEvent.fire( this, event.getValue() );
        checkbox.setFormValue( event.getValue().toString());
    }

    public void setEnsureDebugId(String debugId) {
        checkbox.ensureDebugId(debugId);
    }

    private void initClickHandlers() {
        Event.sinkEvents(number, Event.ONCLICK);
        Event.sinkEvents(status, Event.ONCLICK);
        Event.sinkEvents(name, Event.ONCLICK);
        Event.sinkEvents(amount, Event.ONCLICK);
        Event.setEventListener(number, clickHandler);
        Event.setEventListener(status, clickHandler);
        Event.setEventListener(name, clickHandler);
        Event.setEventListener(amount, clickHandler);
    }

    EventListener clickHandler = new EventListener() {
        @Override
        public void onBrowserEvent(Event event) {
            if (Event.ONCLICK != event.getTypeInt()) {
                return;
            }
            EditEvent.fire(OptionItem.this, null, null);
        }
    };

    @UiField
    CheckBox checkbox;
    @UiField
    SpanElement number;
    @UiField
    Element status;
    @UiField
    SpanElement name;
    @UiField
    SpanElement amount;

    private static OptionItemUiBinder ourUiBinder = GWT.create( OptionItemUiBinder.class );

    interface OptionItemUiBinder extends UiBinder< HTMLPanel, OptionItem > {}

}