package ru.protei.portal.ui.common.client.widget.components.client.selector;

/**
 *  Источник данных для выбора в {@link Selector}
 */
public interface SelectorModel<T>  {

    T get( int elementIndex );
}
