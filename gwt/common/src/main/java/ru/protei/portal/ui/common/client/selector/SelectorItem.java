package ru.protei.portal.ui.common.client.selector;

import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.selector.popup.item.SelectorItemHandler;


public interface SelectorItem<T> extends TakesValue<T>, HasVisibility, HasKeyUpHandlers, SelectorItemChangeHandler, IsWidget {
    void addSelectorHandler(SelectorItemHandler<T> selectorItemHandler );
    void setElementHtml( String elementHtml );
}
