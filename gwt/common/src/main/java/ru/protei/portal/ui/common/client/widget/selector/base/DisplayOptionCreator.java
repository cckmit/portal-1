package ru.protei.portal.ui.common.client.widget.selector.base;

/**
 * Интерфейс для создания модели представления одного элемента списка
 */
public interface DisplayOptionCreator<T> {

    DisplayOption makeDisplayOption( T value );
}
