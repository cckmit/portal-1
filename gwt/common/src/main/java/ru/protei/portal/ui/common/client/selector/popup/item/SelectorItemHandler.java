package ru.protei.portal.ui.common.client.selector.popup.item;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import ru.protei.portal.ui.common.client.selector.SelectorItem;

public interface SelectorItemHandler<T> {
    void onSelectorItemClicked( SelectorItem<T> selectorItem );
    void onKeyboardButtonDown( SelectorItem<T> selectorItem, KeyDownEvent selectorItem );
    void onMouseClickEvent( SelectorItem<T> selectorItem, ClickEvent event );
}
