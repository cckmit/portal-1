package ru.protei.portal.ui.common.client.selector.popup.arrowselectable;

public interface ArrowSelectableSelectorHandler {
    default void escapeFromSelector() {
        onBlurSelector();
    }
    void onBlurSelector();
}
