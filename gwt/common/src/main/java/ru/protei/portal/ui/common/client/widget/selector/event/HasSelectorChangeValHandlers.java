package ru.protei.portal.ui.common.client.widget.selector.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Created by bondarenko on 26.10.16.
 */
public interface HasSelectorChangeValHandlers extends HasHandlers {
    HandlerRegistration addSelectorChangeValHandler(SelectorChangeValHandler handler);
}
