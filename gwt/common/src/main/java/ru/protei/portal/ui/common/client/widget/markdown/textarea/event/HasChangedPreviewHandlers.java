package ru.protei.portal.ui.common.client.widget.markdown.textarea.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasChangedPreviewHandlers extends HasHandlers {
    HandlerRegistration addChangedPreviewHandler(ChangedPreviewHandler handler);
}
