package ru.protei.portal.ui.common.client.widget.imagepastetextarea.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasPasteHandlers extends HasHandlers {

    HandlerRegistration addPasteHandler(PasteHandler handler);
}
