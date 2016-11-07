package ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.handler;

import ru.protei.portal.ui.common.client.widget.autoaddvaluecomment.item.AutoAddVCItem;

/**
 * Обработчик событий при нажтии на кнопку в item'е
 */
public interface ItemChangeHandler {
    void onAdd();
    void onRemove(AutoAddVCItem item);
}
