package ru.protei.portal.ui.common.client.widget.selector.popup.arrowselectable;

public interface ArrowSelectableSelectorHandler {
    default void onInput() {
        onBlurSelector();
    }
    void onBlurSelector();
}
