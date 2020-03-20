package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasCorrectDecimalNumbersHandlers extends HasHandlers {
    HandlerRegistration addCorrectDecimalNumberHandler(CorrectDecimalNumbersHandler handler);
}
