package ru.protei.portal.ui.common.client.widget.components.client.selector;

public interface AsyncSearchSelectorModel<T> {

    T get( int elementIndex, LoadingHandler selector );

    void setSearchString( String searchString );
}
