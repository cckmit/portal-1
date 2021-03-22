package ru.protei.portal.ui.common.client.widget.selector.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasSelectorItemSelectHandlers extends HasHandlers {
    HandlerRegistration addSelectorItemSelectHandler(SelectorItemSelectHandler handler);
}
