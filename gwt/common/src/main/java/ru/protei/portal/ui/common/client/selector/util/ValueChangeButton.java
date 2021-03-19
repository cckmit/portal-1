package ru.protei.portal.ui.common.client.selector.util;

import com.google.gwt.event.dom.client.KeyCodes;

import static java.util.Arrays.stream;

public enum ValueChangeButton {
    ENTER(KeyCodes.KEY_ENTER),
    SPACE(KeyCodes.KEY_SPACE);

    ValueChangeButton(int buttonCode) {
        this.buttonCode = buttonCode;
    }

    private final int buttonCode;

    public int getButtonCode() {
        return buttonCode;
    }

    public static boolean isValueChangeButton(int buttonCode) {
        for (ValueChangeButton value : values()) {
            if(value.getButtonCode() == buttonCode) return true;
        }

        return false;
    }

    public static ValueChangeButton getValueChangeButton(int buttonCode) {
        for (ValueChangeButton value : values()) {
            if(value.getButtonCode() == buttonCode) return value;
        }

        return null;
    }
}
