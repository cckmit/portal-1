package ru.protei.portal.ui.common.client.selector.util;

import com.google.gwt.event.dom.client.KeyCodes;

import static java.util.Arrays.stream;

public enum SelectorItemKeyboardKey {
    ENTER(KeyCodes.KEY_ENTER),
    SPACE(KeyCodes.KEY_SPACE);

    SelectorItemKeyboardKey(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public static boolean isSelectorItemKeyboardKey(int keyCode) {
        for (SelectorItemKeyboardKey value : values()) {
            if (value.getKeyCode() == keyCode) {
                return true;
            }
        }

        return false;
    }

    private final int keyCode;
}
