package ru.protei.portal.ui.web.client.model.event;

import jsinterop.annotations.JsOverlay;

public class EventBusEventTest extends EventBusEvent {
    protected EventBusEventTest() {}
    public static String type = "@app-portal/test-event";

    public static native EventBusEventTest create(String text) /*-{
        return {
            type: @ru.protei.portal.ui.web.client.model.event.EventBusEventTest::type,
            source: @ru.protei.portal.ui.web.client.model.event.EventBusSource::SourcePortalUiGwt,
            payload: {
                text: text
            }
        };
    }-*/;

    @JsOverlay public final native String getText() /*-{ return this.payload.text; }-*/;
}
