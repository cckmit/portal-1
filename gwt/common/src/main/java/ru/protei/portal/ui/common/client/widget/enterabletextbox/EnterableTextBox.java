package ru.protei.portal.ui.common.client.widget.enterabletextbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.helper.HelperFunc;

public class EnterableTextBox extends Composite implements HasValue<String>, HasEnabled {

    public EnterableTextBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public String getValue() {
        return textBox.getValue();
    }

    @Override
    public void setValue(String value) {
        textBox.setValue(value);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        textBox.setValue(value, fireEvents);
    }

    public void setPlaceholder(String value) {
        textBox.getElement().setAttribute("placeholder", value);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        textBox.setEnabled(enabled);
    }

    @UiHandler("textBox")
    public void onTextBoxKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            onEnter();
        }
    }

    @UiHandler("textBoxAction")
    public void onTextBoxActionClick(ClickEvent event) {
        event.preventDefault();
        onEnter();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addDomHandler(handler, ChangeEvent.getType());
    }

    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return textBox.addKeyDownHandler(handler);
    }

    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return textBox.addKeyPressHandler(handler);
    }

    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return textBox.addKeyUpHandler(handler);
    }

    public void setFocus(boolean focused) {
        textBox.setFocus(focused);
    }

    private void onEnter() {
        if (!enabled) {
            return;
        }
        ValueChangeEvent.fire(EnterableTextBox.this, getValue());
    }

    @UiField
    TextBox textBox;

    @UiField
    Anchor textBoxAction;

    private boolean enabled = true;

    interface EnterableTextBoxViewUiBinder extends UiBinder<HTMLPanel, EnterableTextBox> {}
    private static EnterableTextBoxViewUiBinder ourUiBinder = GWT.create( EnterableTextBoxViewUiBinder.class );
}
