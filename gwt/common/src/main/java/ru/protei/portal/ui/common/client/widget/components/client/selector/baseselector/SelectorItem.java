package ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector;

import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface SelectorItem<T> extends TakesValue<T>, HasVisibility, HasKeyUpHandlers, IsWidget {
    void addSelectorHandler( SelectorItemHandler selectorItemHandler );

    void setElementHtml( String elementHtml );

    void setElementWidget( Widget widget );

    void setFocus( boolean isFocused );
}
