package ru.protei.portal.ui.common.client.widget.delivery.optionlist.item;

import com.google.gwt.core.client.GWT;
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
import ru.protei.portal.ui.common.client.widget.selector.event.HasRemoveHandlers;
import ru.protei.portal.ui.common.client.widget.selector.event.RemoveEvent;
import ru.protei.portal.ui.common.client.widget.selector.event.RemoveHandler;

/**
 * Однострочный элемент списка с серийным номером, статусом, именем и кнопкой редактирования
 */
public class OptionItem
        extends Composite
        implements HasValue<Boolean>, HasEnabled, HasRemoveHandlers
{
    public OptionItem() {
        initWidget( ourUiBinder.createAndBindUi( this ) );

        initClickHandlers();
    }

    public void setNumber( String number ) {
        if (number == null) return;
        this.number.setInnerText( number );
    }

    public void setStatus( String status ) {
        if (status == null) return;
        this.status.setInnerText( status );
    }

    public void setName( String name ) {
        if (name == null) return;
        this.name.setInnerText( name );
    }

    public void setStatusColor( String color ) {
        this.status.getStyle().setBackgroundColor( color );
    }

    public void setItemEditable( boolean isItemEditable ) {
        edit.setVisible(isItemEditable);
    }

    @Override
    public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
        return addHandler(handler, RemoveEvent.getType());
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

    @UiHandler("edit")
    public void onEditClicked(ClickEvent event) {
        event.preventDefault();
        RemoveEvent.fire(this);
    }

    public void setEnsureDebugId(String debugId) {
        checkbox.ensureDebugId(debugId);
    }

    private void initClickHandlers() {
        Event.sinkEvents(number, Event.ONCLICK);
        Event.sinkEvents(status, Event.ONCLICK);
        Event.sinkEvents(name, Event.ONCLICK);
        Event.setEventListener(number, clickHandler);
        Event.setEventListener(status, clickHandler);
        Event.setEventListener(name, clickHandler);
    }

    EventListener clickHandler = new EventListener() {
        @Override
        public void onBrowserEvent(Event event) {
            if (Event.ONCLICK != event.getTypeInt()) {
                return;
            }
            if (!checkbox.isEnabled()) {
                return;
            }
            checkbox.setValue(!checkbox.getValue(), true);
        }
    };

    @UiField
    CheckBox checkbox;
    @UiField
    SpanElement number;
    @UiField
    SpanElement status;
    @UiField
    SpanElement name;
    @UiField
    Anchor edit;

    private static OptionItemUiBinder ourUiBinder = GWT.create( OptionItemUiBinder.class );
    interface OptionItemUiBinder extends UiBinder< HTMLPanel, OptionItem > {}

}