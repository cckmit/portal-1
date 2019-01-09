package ru.protei.portal.ui.common.client.widget.selector.base;


public interface SelectorModel<T> {
    void onSelectorLoad( HasSelectableValues<T> iSelector );
}
