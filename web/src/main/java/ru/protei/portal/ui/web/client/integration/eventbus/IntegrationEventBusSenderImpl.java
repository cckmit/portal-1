package ru.protei.portal.ui.web.client.integration.eventbus;

import com.google.gwt.core.client.JavaScriptObject;
import ru.protei.portal.ui.web.client.model.connector.ConnectorMessage;
import ru.protei.portal.ui.web.client.model.event.EventBusEvent;

public class IntegrationEventBusSenderImpl implements IntegrationEventBusSender {

    public IntegrationEventBusSenderImpl() {
    }

    @Override
    public void fireEvent(EventBusEvent event) {
        ConnectorMessage message = makeEventbusMessage(event);
        send(message);
    }

    private native ConnectorMessage makeEventbusMessage(JavaScriptObject event)/*-{
        return {
            type: "eventbus-event",
            payload: event
        }
    }-*/;

    private native void send(ConnectorMessage message)/*-{
        if (!$wnd.Protei_PORTAL_Bridge) {
            return
        }
        if (typeof $wnd.Protei_PORTAL_Bridge.messageIn === "function") {
            $wnd.Protei_PORTAL_Bridge.messageIn(message)
        }
    }-*/;
}
