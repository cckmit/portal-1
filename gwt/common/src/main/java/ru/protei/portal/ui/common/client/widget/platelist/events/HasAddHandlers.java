package ru.protei.portal.ui.common.client.widget.platelist.events;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasAddHandlers extends HasHandlers {
    HandlerRegistration addAddHandler( AddHandler handler );
}
