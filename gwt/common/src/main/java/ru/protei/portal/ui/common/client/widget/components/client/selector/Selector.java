package ru.protei.portal.ui.common.client.widget.components.client.selector;

/**
 * Механизм предоставления списка выбора.
 * Требует источника данных для элементов {@link SelectorModel}
 * Требует способ формирования содержимого одного элемента {@link SelectorItemRenderer}
 */
public interface Selector<T>
{
    void setSelectorModel( SelectorModel<T> selectorModel );

    void setSelectorItemRenderer( SelectorItemRenderer<T> selectorItemRenderer );
}
