package ru.protei.portal.ui.common.client.widget.markdown.textarea.event;

import com.google.gwt.event.shared.GwtEvent;

public class ChangedPreviewEvent extends GwtEvent<ChangedPreviewHandler> {

    private static Type<ChangedPreviewHandler> TYPE = new Type<>();
    private final String previewText;

    private ChangedPreviewEvent(String previewText) {
        this.previewText = previewText;
    }

    public static void fire(HasChangedPreviewHandlers source, String formattedText) {
        if (TYPE != null) {
            source.fireEvent(new ChangedPreviewEvent(formattedText));
        }
    }

    public static Type<ChangedPreviewHandler> getType() {
        return TYPE;
    }

    @Override
    public Type<ChangedPreviewHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ChangedPreviewHandler handler) {
        handler.onChangedPreview(this);
    }

    public String getPreviewText() {
        return previewText;
    }
}
