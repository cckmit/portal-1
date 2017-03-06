package ru.protei.portal.ui.common.client.widget.selector.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Обработчик RemoveEvent
 */
public interface RemoveHandler extends EventHandler {
    void onRemove( RemoveEvent event );
}