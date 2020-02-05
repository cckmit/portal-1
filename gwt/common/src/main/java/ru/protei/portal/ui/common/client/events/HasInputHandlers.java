package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.HandlerRegistration;

public interface HasInputHandlers {
    HandlerRegistration addInputHandler(InputHandler handler);
}
