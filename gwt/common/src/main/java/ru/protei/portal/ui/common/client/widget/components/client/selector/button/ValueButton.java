package ru.protei.portal.ui.common.client.widget.components.client.selector.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * "Значение" в кнопке
 */
public class ValueButton extends Composite
        implements TakesValue<String>, HasEnabled, HasClickHandlers {

    public ValueButton() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setValue(String innerHtml) {
        value.setInnerHTML(innerHtml);
    }

    @Override
    public String getValue() {
        return value.getInnerHTML();
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        button.setEnabled(isEnabled);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
        return button.addClickHandler(clickHandler);
    }

    interface ValueButtonUiBinder extends UiBinder<Button, ValueButton> {
    }

    private static ValueButtonUiBinder ourUiBinder = GWT.create(ValueButtonUiBinder.class);

    @UiField
    Button button;
    @UiField
    SpanElement value;

}