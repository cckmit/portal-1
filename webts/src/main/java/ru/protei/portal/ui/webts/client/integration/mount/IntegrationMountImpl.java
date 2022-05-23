package ru.protei.portal.ui.webts.client.integration.mount;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import ru.protei.portal.ui.webts.client.model.TsWebUnit;
import ru.protei.portal.ui.webts.client.model.connector.ConnectorMessage;

public class IntegrationMountImpl implements IntegrationMount {

    @Override
    public void mount(Element root, TsWebUnit unit, EventTarget emitter) {
        ConnectorMessage message = makeMountMessage(root, unit.getName(), emitter);
        send(message);
    }

    private native ConnectorMessage makeMountMessage(Element container, String unit, EventTarget emitter)/*-{
        return {
            type: "mount",
            payload: {
                container: container,
                unit: unit,
                emitter: emitter
            }
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
