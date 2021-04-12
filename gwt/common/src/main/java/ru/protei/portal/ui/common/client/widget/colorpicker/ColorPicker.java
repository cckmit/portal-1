package ru.protei.portal.ui.common.client.widget.colorpicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.colorpicker.popup.ColorPickerPopup;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

public class ColorPicker extends Composite implements HasEnabled, HasValue<String>, ValueChangeHandler<String> {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        colorBox.getElement().setAttribute("placeholder", lang.colorHex());
    }

    @Override
    public String getValue() {
        return colorBox.getValue();
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        resetTimer();
        colorBox.setValue(value, fireEvents);

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        String value = event.getValue();
        setValue(value);
    }

    @Override
    public boolean isEnabled() {
        return colorPickerButton.isEnabled() && colorBox.isEnabled();
    }

    @Override
    public void setEnabled(boolean isEnabled){
        colorPickerButton.setEnabled(isEnabled);
        colorBox.setEnabled(isEnabled);
    }

    @UiHandler("colorPickerButton")
    public void colorPickerButtonClick(ClickEvent event) {
        if (colorPickerButton.isEnabled()) {
            showPopup(colorPickerButton);
        }
    }

    @UiHandler("colorBox")
    public void onColorBoxValueChanged(KeyUpEvent event) {
        resetTimer();
    }

    private void ensureDebugIds() {
        colorPickerButton.ensureDebugId(DebugIds.COLOR_PICKER.BUTTON);
        colorBox.ensureDebugId(DebugIds.COLOR_PICKER.INPUT);
    }

    private void showPopup(IsWidget relative) {
        if (popupValueChangeHandlerRegistration != null) {
            popupValueChangeHandlerRegistration.removeHandler();
        }
        popupValueChangeHandlerRegistration = popup.addValueChangeHandler(this);
        popup.showNear(relative);
    }

    public static boolean isValidHexColor(String color) {
        return !isBlank(color) && HEX_REGEX.exec(color) != null;
    }

    private void setColorRequired(boolean required) {
        if (required) {
            colorBox.addStyleName("required");
        } else {
            colorBox.removeStyleName("required");
        }
    }

    @Inject
    Lang lang;
    @Inject
    ColorPickerPopup popup;

    @UiField
    HTMLPanel root;
    @UiField
    ValidableTextBox colorBox;
    @UiField
    Button colorPickerButton;

    Timer timer = new Timer() {

        @Override
        public void run() {
            setColorRequired(!isValidHexColor(colorBox.getValue()));
        }
    };

    private void resetTimer() {
        timer.scheduleRepeating(1);
    }

    private HandlerRegistration popupValueChangeHandlerRegistration;

    private static final RegExp HEX_REGEX = RegExp.compile("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$");

    interface ColorPickerUiBinder extends UiBinder<HTMLPanel, ColorPicker> {}
    private static ColorPickerUiBinder ourUiBinder = GWT.create(ColorPickerUiBinder.class);
}
