package ru.protei.portal.ui.web.client.model.event;

public class TestEventBusEvent extends EventBusEvent {
    public static String type = "@app-portal/test-event";

    public static native TestEventBusEvent create(String text) /*-{
        return {
            type: @ru.protei.portal.ui.web.client.model.event.TestEventBusEvent::type,
            source: @ru.protei.portal.ui.web.client.model.event.EventBusSource::SourcePortalUiGwt,
            payload: {
                text: text
            }
        };
    }-*/;

    public final native String getText() /*-{
        return this.payload.text;
    }-*/;

    protected TestEventBusEvent() {}
}
