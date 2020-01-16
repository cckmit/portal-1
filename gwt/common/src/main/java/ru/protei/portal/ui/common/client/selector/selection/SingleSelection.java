package ru.protei.portal.ui.common.client.selector.selection;

/**
 * Выбранное значения
 */
public interface SingleSelection<T> extends Selection<T> {

    T get();
}
