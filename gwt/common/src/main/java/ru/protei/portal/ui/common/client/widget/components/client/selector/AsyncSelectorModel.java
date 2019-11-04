package ru.protei.portal.ui.common.client.widget.components.client.selector;

public interface AsyncSelectorModel<T> {

    T get( int elementIndex, LoadingHandler selector );
}
