package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasRejectHandlers extends HasHandlers {
    HandlerRegistration addRejectHandler(RejectHandler handler);
}
