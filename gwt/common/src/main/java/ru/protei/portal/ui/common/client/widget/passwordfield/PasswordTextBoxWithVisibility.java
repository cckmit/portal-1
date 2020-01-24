package ru.protei.portal.ui.common.client.widget.passwordfield;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.ui.common.client.events.HasInputHandlers;
import ru.protei.portal.ui.common.client.events.InputEvent;
import ru.protei.portal.ui.common.client.events.InputHandler;

public class PasswordTextBoxWithVisibility extends Composite implements HasClickHandlers, HasText, HasPasswordVisibility, HasInputHandlers {
    public PasswordTextBoxWithVisibility() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public String getText() {
        return password.getText();
    }

    @Override
    public void setText(String text) {
        password.setText(text);
    }

    @Override
    public void setPasswordVisible(boolean isVisible) {
        showPassword.setValue(isVisible, true);
    }

    @Override
    public boolean isPasswordVisible() {
        return showPassword.getValue();
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addHandler(handler, ClickEvent.getType());
    }

    @Override
    public HandlerRegistration addInputHandler(InputHandler handler) {
        return addHandler(handler, InputEvent.getType());
    }

    @UiHandler("showPassword")
    public void onShowPasswordToggleChanged(ValueChangeEvent<Boolean> event) {
        password.getElement().setAttribute("type", event.getValue() ? "text" : "password");
    }

    @UiHandler("password")
    public void onPasswordClicked(ClickEvent event) {
        ClickEvent.fireNativeEvent(event.getNativeEvent(), this);
    }

    @UiField
    PasswordTextBox password;

    @UiField
    ToggleButton showPassword;

    interface PasswordTextBoxWithVisibilityUiBinder extends UiBinder<HTMLPanel, PasswordTextBoxWithVisibility> {
    }

    private static PasswordTextBoxWithVisibilityUiBinder ourUiBinder = GWT.create(PasswordTextBoxWithVisibilityUiBinder.class);
}