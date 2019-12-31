package ru.protei.portal.ui.common.client.widget.components.client.selector;

/**
 * Формирование содержимого одного элемента выбора для типа T
 * Достаточно сформированть название элемента.
 */
public interface SelectorItemRenderer<T> {
    String getElementName( T t );

    default String getElementHtml( T t ) {
        return getElementName( t );
    }
}
