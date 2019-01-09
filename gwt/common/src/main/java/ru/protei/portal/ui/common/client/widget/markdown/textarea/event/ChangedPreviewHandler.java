package ru.protei.portal.ui.common.client.widget.markdown.textarea.event;

import com.google.gwt.event.shared.EventHandler;

public interface ChangedPreviewHandler extends EventHandler {
    void onChangedPreview(ChangedPreviewEvent event);
}
