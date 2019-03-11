package ru.protei.portal.ui.common.client.widget.imagepastetextarea.event;

import com.google.gwt.event.shared.EventHandler;

public interface PasteHandler extends EventHandler {
    void onRemove(PasteEvent event);
}
