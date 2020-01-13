package ru.protei.portal.ui.common.client.selector.selection;

/**
 * Выбранное/ые значения
 */
public interface Selection<T>{
    void select( T value );

    boolean isSelected( T t );

    boolean isEmpty();

    void clear();
}
