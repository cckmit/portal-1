package ru.protei.portal.ui.common.client.events.clone;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasCloneHandlers extends HasHandlers {
    HandlerRegistration addCloneHandler(CloneHandler handler);
}
