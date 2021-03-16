package ru.protei.portal.ui.common.client.selector.util;

import static java.util.Arrays.stream;

public class ValueChangeButtonUtil {
    public static boolean isValueChangeButton(int buttonCode) {
        return stream(ValueChangeButton.values())
                .anyMatch(valueChangeButton -> valueChangeButton.getButtonCode() == buttonCode);
    }
}
