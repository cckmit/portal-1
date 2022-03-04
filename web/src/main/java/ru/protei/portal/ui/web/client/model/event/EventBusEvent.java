package ru.protei.portal.ui.web.client.model.event;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsOverlay;

public abstract class EventBusEvent extends JavaScriptObject {
    protected EventBusEvent() {
    }

    @JsOverlay
    public final native String getType() /*-{
        return this.type;
    }-*/;

    @JsOverlay
    public final native String getSource() /*-{
        return this.source;
    }-*/;

    @JsOverlay
    public final native JavaScriptObject getPayload() /*-{
        return this.payload;
    }-*/;
}
