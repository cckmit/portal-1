package ru.protei.portal.ui.common.client.widget.selector.base;


public interface SelectorModel<T> {
    void onSelectorLoad( SelectorWithModel<T> selector );
    default void onSelectorUnload( SelectorWithModel<T> selector ){};
}
