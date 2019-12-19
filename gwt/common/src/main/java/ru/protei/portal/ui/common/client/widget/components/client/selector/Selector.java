package ru.protei.portal.ui.common.client.widget.components.client.selector;

public interface Selector<T>
{
    void setSelectorModel( SelectorModel<T> selectorModel );

    void setSelectorItemRenderer( SelectorItemRenderer<T> selectorItemRenderer );
}
