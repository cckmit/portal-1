package ru.protei.portal.ui.common.client.widget.components.client.selector.baseselector;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

public interface SelectorItem<T> extends TakesValue<T>, HasVisibility, IsWidget {
    void addSelectorHandler( SelectorItemHandler selectorItemHandler );

    void setElementHtml( String elementHtml );
}
