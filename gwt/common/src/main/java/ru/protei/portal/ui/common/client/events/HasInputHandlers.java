package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasInputHandlers extends HasHandlers {
    HandlerRegistration addInputHandler(InputHandler handler);
}
