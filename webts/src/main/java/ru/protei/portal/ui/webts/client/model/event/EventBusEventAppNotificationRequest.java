package ru.protei.portal.ui.webts.client.model.event;

import jsinterop.annotations.JsOverlay;

public class EventBusEventAppNotificationRequest extends EventBusEvent {
    protected EventBusEventAppNotificationRequest() {}
    public static String type = "@app-portal/notification-request";

    @JsOverlay public final native String getNotificationId() /*-{ return this.payload.notification.notificationId; }-*/;
    @JsOverlay public final native String getNotificationType() /*-{ return this.payload.notification.type; }-*/;
    @JsOverlay public final native String getNotificationMessage() /*-{ return this.payload.notification.message; }-*/;
}
