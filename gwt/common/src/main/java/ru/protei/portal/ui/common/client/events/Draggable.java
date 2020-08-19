package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.dom.client.HasDragEndHandlers;
import com.google.gwt.event.dom.client.HasDragOverHandlers;
import com.google.gwt.event.dom.client.HasDragStartHandlers;
import com.google.gwt.event.dom.client.HasDropHandlers;

public interface Draggable extends HasDragStartHandlers, HasDragEndHandlers, HasDragOverHandlers, HasDropHandlers {
}
