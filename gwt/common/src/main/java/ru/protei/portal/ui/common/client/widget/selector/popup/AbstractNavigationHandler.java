package ru.protei.portal.ui.common.client.widget.selector.popup;

/**
 * Хэндлер для управления навигацией в селекторе
 */
public interface AbstractNavigationHandler {

    void onArrowUp();

    void onArrowDown();

    void onEnterClicked();

    void selectFirst();
}
