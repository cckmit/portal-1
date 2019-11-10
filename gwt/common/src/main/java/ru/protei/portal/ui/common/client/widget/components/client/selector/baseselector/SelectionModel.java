package ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector;

public interface SelectionModel<T>{
    void select( T value );

    boolean isSelected( T t );

    boolean isEmpty();

    void clear();
}
