package ru.protei.portal.ui.web.client.model.event;

import jsinterop.annotations.JsOverlay;

public class EventBusEventAuthLogoutDone extends EventBusEvent {
    protected EventBusEventAuthLogoutDone() {}
    public static String type = "@app-portal/auth-logout-done";

    public static native EventBusEventAuthLogoutDone create(boolean userInitiated) /*-{
        return {
            type: @ru.protei.portal.ui.web.client.model.event.EventBusEventAuthLogoutDone::type,
            source: @ru.protei.portal.ui.web.client.model.event.EventBusSource::SourcePortalUiGwt,
            payload: {
                userInitiated: userInitiated
            }
        };
    }-*/;

    @JsOverlay public final native boolean isUserInitiated() /*-{ return this.payload.userInitiated; }-*/;
}
