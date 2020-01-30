package ru.protei.portal.ui.common.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface RejectHandler extends EventHandler {
    void onReject(RejectEvent event);
}
