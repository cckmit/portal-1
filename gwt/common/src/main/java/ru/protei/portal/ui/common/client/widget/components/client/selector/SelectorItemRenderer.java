package ru.protei.portal.ui.common.client.widget.components.client.selector;

public interface SelectorItemRenderer<T> {
    String getElementName( T t );

    default String getElementHtml( T t ) {
        return getElementName( t );
    }
}
