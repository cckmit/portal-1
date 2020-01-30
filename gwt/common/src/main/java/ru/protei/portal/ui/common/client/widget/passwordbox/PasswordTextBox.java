package ru.protei.portal.ui.common.client.widget.passwordbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.events.HasInputHandlers;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.InputHandler;

public class PasswordTextBox extends Composite implements HasFocusHandlers, HasValue<String>, HasInputHandlers {

    public PasswordTextBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public String getValue() {
        return password.getValue();
    }

    @Override
    public void setValue(String value) {
        password.setValue(value);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        password.setValue(value, fireEvents);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return password.addValueChangeHandler(handler);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return password.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addInputHandler(InputHandler handler) {
        return password.addDomHandler(handler, InputEvent.getType());
    }

    public void setFocus(boolean isFocus) {
        password.setFocus(isFocus);
    }

    @UiHandler("showPassword")
    public void onShowPasswordClicked(ValueChangeEvent<Boolean> event) {
        showPassword(event.getValue());
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        showPassword(false);
        showPassword.setValue(false);
    }

    private void showPassword(boolean isVisible) {
        password.getElement().setAttribute("type", isVisible ? "text" : "password");
    }

    @UiField
    com.google.gwt.user.client.ui.PasswordTextBox password;

    @UiField
    ToggleButton showPassword;

    interface PasswordTextBoxUiBinder extends UiBinder<HTMLPanel, PasswordTextBox> {}
    private static PasswordTextBoxUiBinder ourUiBinder = GWT.create(PasswordTextBoxUiBinder.class);
}