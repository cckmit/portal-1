package ru.protei.portal.ui.common.client.widget.markdown.textarea;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.markdown.Markdown;
import ru.protei.portal.ui.common.client.widget.markdown.textarea.event.*;

public class MarkdownTextArea extends AutoResizeTextArea implements HasAddHandlers, HasChangedPreviewHandlers {

    private final static int TEXTAREA_VALUE_CHANGE_EVENTS = Event.ONKEYUP | Event.ONCHANGE | Event.ONPASTE;
    private final static int PREVIEW_CHANGE_DELAY_MS = 200;

    public MarkdownTextArea() {
        super();
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        super.setValue(value, fireEvents);
        fireChangedPreview();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        sinkEvents(TEXTAREA_VALUE_CHANGE_EVENTS);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if ((DOM.eventGetType(event) & TEXTAREA_VALUE_CHANGE_EVENTS) != 0) {
            scheduleChangedPreview();
        }
    }

    @Override
    public HandlerRegistration addChangedPreviewHandler(ChangedPreviewHandler handler) {
        return addHandler(handler, ChangedPreviewEvent.getType());
    }

    private void scheduleChangedPreview() {
        changedPreviewTimer.cancel();
        changedPreviewTimer.schedule(PREVIEW_CHANGE_DELAY_MS);
    }

    private void fireChangedPreview() {
        String value = getValue();
        if (StringUtils.isBlank(value)) {
            ChangedPreviewEvent.fire(MarkdownTextArea.this, "");
            return;
        }
        String formatted = Markdown.plain2escaped2markdown(value);
        ChangedPreviewEvent.fire(MarkdownTextArea.this, formatted);
    }

    private final Timer changedPreviewTimer = new Timer() {
        @Override
        public void run() {
            fireChangedPreview();
        }
    };
}
