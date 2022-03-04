package ru.protei.portal.ui.web.client.integration.mount;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import ru.protei.portal.ui.web.client.model.TsWebUnit;
import ru.protei.portal.ui.web.client.model.connector.ConnectorMessage;

public class IntegrationMountImpl implements IntegrationMount {

    @Override
    public void mount(Element root, TsWebUnit unit, EventTarget emitter) {
        ConnectorMessage message = makeMountMessage(root, unit.getName(), emitter);
        send(message);
    }

    private native ConnectorMessage makeMountMessage(Element container, String unit, EventTarget emitter)/*-{
        return {
            type: "portal-mount",
            payload: {
                container: container,
                unit: unit,
                emitter: emitter
            }
        }
    }-*/;

    private native void send(ConnectorMessage message)/*-{
        if (!$wnd.ProteiPortalGwtBridge) {
            return
        }
        if (typeof $wnd.ProteiPortalGwtBridge.messageIn === "function") {
            $wnd.ProteiPortalGwtBridge.messageIn(message)
        }
    }-*/;
}
