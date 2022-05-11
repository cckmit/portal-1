package ru.protei.portal.ui.web.client.model.event;

import jsinterop.annotations.JsOverlay;

public class EventBusEventAuthLoginDone extends EventBusEvent {
    protected EventBusEventAuthLoginDone() {}
    public static String type = "@app-portal/auth-login-done";

    public static native EventBusEventAuthLoginDone create(double personId, double loginId) /*-{
        return {
            type: @ru.protei.portal.ui.web.client.model.event.EventBusEventAuthLoginDone::type,
            source: @ru.protei.portal.ui.web.client.model.event.EventBusSource::SourcePortalUiGwt,
            payload: {
                personId: personId,
                loginId: loginId
            }
        };
    }-*/;

    @JsOverlay public final native Long getPersonId() /*-{ return this.payload.personId; }-*/;
    @JsOverlay public final native Long getLoginId() /*-{ return this.payload.loginId; }-*/;
}
