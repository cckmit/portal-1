package ru.protei.portal.ui.common.client.selector.pageable;

/**
 *  Источник данных для выбора в {@link Selector}
 */
public interface SelectorModel<T>  {

    T get( int elementIndex );
}
