package ru.protei.portal.ui.common.client.selector.logic;

/**
 * Механизм предоставления списка выбора.
 * Требует источника данных для элементов {@link SelectorModel}
 * Требует способ формирования содержимого одного элемента {@link SelectorItemRenderer}
 */
public interface Selector<T> {
    void setModel( SelectorModel<T> selectorModel );

    void setItemRenderer( SelectorItemRenderer<T> selectorItemRenderer );
}
