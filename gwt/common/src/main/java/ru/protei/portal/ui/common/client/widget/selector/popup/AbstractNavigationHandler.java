package ru.protei.portal.ui.common.client.widget.selector.popup;

import ru.protei.portal.ui.common.client.widget.selector.item.SelectorItem;

/**
 * Хэндлер для управления навигацией в селекторе
 */
public interface AbstractNavigationHandler {

    void onArrowUp(SelectorItem selectorItem);

    void onArrowDown(SelectorItem selectorItem);
}
