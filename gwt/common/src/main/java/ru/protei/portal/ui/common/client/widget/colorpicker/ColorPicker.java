package ru.protei.portal.ui.common.client.widget.colorpicker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.colorpicker.popup.ColorPickerPopup;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

public class ColorPicker extends Composite implements HasEnabled, HasValue<String>, ValueChangeHandler<String> {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
        colorBox.getElement().setAttribute("placeholder", lang.colorHex());
    }

    @Override
    public String getValue() {
        String value = colorBox.getValue();
        if (!validateHexColor(value)) {
            return null;
        }
        return value;
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        if (validateHexColor(value)) {
            colorBox.setValue(value, fireEvents);
        }
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

    @UiHandler("colorPickerButton")
    public void colorPickerButtonClick(ClickEvent event) {
        if(colorPickerButton.isEnabled()) {
            showPopup(colorPickerButton);
        }
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

    private boolean validateHexColor(String hexColor) {
        return StringUtils.isBlank(hexColor) || HEX_REGEX.exec(hexColor) != null;
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

    private HandlerRegistration popupValueChangeHandlerRegistration;

    private static final RegExp HEX_REGEX = RegExp.compile("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$");

    interface ColorPickerUiBinder extends UiBinder<HTMLPanel, ColorPicker> {}
    private static ColorPickerUiBinder ourUiBinder = GWT.create(ColorPickerUiBinder.class);
}
