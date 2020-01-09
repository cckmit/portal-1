package ru.protei.portal.ui.common.client.selector;

import ru.protei.portal.ui.common.client.selector.LoadingHandler;

public interface AsyncSearchSelectorModel<T> {

    T get( int elementIndex, LoadingHandler selector );

    void setSearchString( String searchString );
}
