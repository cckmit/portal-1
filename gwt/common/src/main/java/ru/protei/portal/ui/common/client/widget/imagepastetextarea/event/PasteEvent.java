package ru.protei.portal.ui.common.client.widget.imagepastetextarea.event;

import com.google.gwt.event.shared.GwtEvent;
import ru.protei.portal.core.model.struct.Base64Facade;

public class PasteEvent extends GwtEvent<PasteHandler> {
    private static Type<PasteHandler> TYPE = new Type<>();
    private String json;

    public static void fire(HasPasteHandlers source, String json) {
        if (TYPE != null) {
            source.fireEvent(new PasteEvent(json));
        }
    }

    public static Type<PasteHandler> getType() {
        return TYPE;
    }

    private PasteEvent(String json) {
        this.json = json;
    }

    protected void dispatch(PasteHandler handler) {
        handler.onRemove(this);
    }

    public final Type<PasteHandler> getAssociatedType() {
        return TYPE;
    }

    public String toDebugString() {
        return super.toDebugString();
    }

    public String getJson() {
        return json;
    }
}
