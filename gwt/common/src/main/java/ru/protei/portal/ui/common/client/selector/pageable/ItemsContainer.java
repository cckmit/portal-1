package ru.protei.portal.ui.common.client.selector.pageable;

public interface ItemsContainer<T> {

    void fill( T element, String elementHtml, String name );
}
