package ru.protei.portal.ui.product.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by frost on 10/4/16.
 */
public class CheckBoxWidget extends Composite implements HasValue<Boolean>, HasClickHandlers, ClickHandler {

    public CheckBoxWidget() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        initHandler();
    }

    @Override
    public Boolean getValue() {
        return check.getStyleName().contains("active");
    }

    @Override
    public void setValue(Boolean value) {
        setValue( value, false );
    }

    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        if ( value ) {
            check.addStyleName("active");
        }
        else {
            check.removeStyleName("active");
        }

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler( handler, ClickEvent.getType() );
    }

    private void initHandler ()
    {
        check.sinkEvents(Event.ONCLICK);
        check.addHandler(this, ClickEvent.getType());
    }

    @UiField
    HTMLPanel check;

    @Override
    public void onClick(ClickEvent event) {
        setValue(!getValue(), true);
    }

    interface CheckBoxViewUiBinder extends UiBinder<HTMLPanel, CheckBoxWidget > {}
    private static CheckBoxViewUiBinder ourUiBinder = GWT.create( CheckBoxViewUiBinder.class );
}