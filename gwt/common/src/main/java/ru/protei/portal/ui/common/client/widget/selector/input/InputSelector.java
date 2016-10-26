package ru.protei.portal.ui.common.client.widget.selector.input;

import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Button селектор с включенным поиском
 */
public class InputSelector<T> extends ButtonSelector<T> {
    public InputSelector() {
        setSearchEnabled(true);
        setSearchAutoFocus(true);
    }
}
