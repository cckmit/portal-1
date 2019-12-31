package ru.protei.portal.ui.common.client.widget.components.client.selector.impl;

import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.widget.components.client.selector.AsyncSearchSelectorModel;
import ru.protei.portal.ui.common.client.widget.components.client.selector.AsyncSelectorModel;
import ru.protei.portal.ui.common.client.widget.components.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.widget.components.client.selector.SelectorModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Общие методы модели селектора
 */
public abstract class BaseSelectorModel<T> implements SelectorModel<T>, AsyncSelectorModel<T>, AsyncSearchSelectorModel<T> {

    @Override
    public T get( int elementIndex ) {
        return CollectionUtils.get( elements, elementIndex );
    }

    @Override
    public T get( int elementIndex, LoadingHandler selector ) {
        if (elements == null) {
            selector.onLoadingStart();
            requestData( selector, searchString );
            return null;
        }

        return get( elementIndex );
    }

    @Override
    public void setSearchString( String searchString ) {
        clean();
        this.searchString = searchString;
    }

    public void clean() {
        elements = null;
    }

    protected void requestData( LoadingHandler selector, String searchText ){}

    protected void updateElements( Collection<T> result ) {
        updateElements( result, null );
    }

    protected void updateElements( Collection<T> result, LoadingHandler selector ) {
        if (elements == null) {
            elements = new ArrayList<T>();
        } else {
            elements.clear();
        }
        elements.addAll( result );

        if (selector != null){
            selector.onLoadingComplete();
        }
    }

    protected List<T> elements;
    private String searchString = null;
}
