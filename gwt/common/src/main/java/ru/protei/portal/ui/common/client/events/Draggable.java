package ru.protei.portal.ui.common.client.events;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;

public interface Draggable extends HasDragStartHandlers, HasDragEndHandlers, HasDragOverHandlers, HasDropHandlers {
    Element getInnerContainer();
}
