package ru.protei.portal.ui.web.client.model.connector;

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsOverlay;

public class ConnectorMessage extends JavaScriptObject {
    protected ConnectorMessage() {}

    @JsOverlay public final native String getType() /*-{ return this.type; }-*/;
    @JsOverlay public final native JavaScriptObject getPayload() /*-{ return this.payload; }-*/;
}
