package ru.protei.portal.ui.common.client.selector.popup.item;

import ru.protei.portal.ui.common.client.selector.SelectorItem;

public interface SelectorItemHandler<T> {
    void onSelectorItemClicked( SelectorItem<T> selectorItem );
}
