package ru.protei.portal.ui.web.client.integration;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import ru.protei.portal.ui.web.client.integration.eventbus.IntegrationEventBusImpl;
import ru.protei.portal.ui.web.client.integration.mount.IntegrationMountImpl;
import ru.protei.portal.ui.web.client.model.MountDescriptor;
import ru.protei.portal.ui.web.client.model.TsWebUnit;
import ru.protei.portal.ui.web.client.model.Unsubscribe;
import ru.protei.portal.ui.web.client.model.connector.ConnectorMessage;
import ru.protei.portal.ui.web.client.model.event.EventBusEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NativeWebIntegrationImpl implements NativeWebIntegration {

    private final IntegrationMountImpl integrationMount;
    private final IntegrationEventBusImpl integrationEventBus;
    private boolean loaded;
    private final List<EventBusEvent> eventBusEventsFiredBeforeLoad;
    private final List<MountDescriptor> mountDescriptorsFiredBeforeLoad;

    public NativeWebIntegrationImpl() {
        integrationMount = new IntegrationMountImpl();
        integrationEventBus = new IntegrationEventBusImpl();
        loaded = false;
        eventBusEventsFiredBeforeLoad = new ArrayList<>();
        mountDescriptorsFiredBeforeLoad = new ArrayList<>();
    }

    @Override
    public void setup() {
        setupNative();
        loaded = true;
        for (EventBusEvent event : eventBusEventsFiredBeforeLoad) {
            fireEvent(event);
        }
        for (MountDescriptor mountDescriptor : mountDescriptorsFiredBeforeLoad) {
            mount(mountDescriptor.root, mountDescriptor.unit, mountDescriptor.emitter);
        }
        eventBusEventsFiredBeforeLoad.clear();
    }

    private native void setupNative()/*-{
        var self = this;
        if (!$wnd.ProteiPortalGwtBridge) {
            $wnd.ProteiPortalGwtBridge = {};
        }
        $wnd.ProteiPortalGwtBridge.messageOut = $entry(function (message) {
            self.@ru.protei.portal.ui.web.client.integration.NativeWebIntegrationImpl::onConnectorMessage(*)(message);
        });
    }-*/;

    private void onConnectorMessage(ConnectorMessage message) {
        String type = message.getType();
        if (type == null) {
            return;
        }
        switch (type) {
            case "portal-eventbus-event": {
                EventBusEvent event = message.getPayload().cast();
                integrationEventBus.invokeListeners(event);
                break;
            }
            case "portal-mount": {
                // do nothing
                break;
            }
        }
    }

    @Override
    public void fireEvent(EventBusEvent event) {
        if (!loaded) {
            eventBusEventsFiredBeforeLoad.add(event);
            return;
        }
        integrationEventBus.fireEvent(event);
    }

    @Override
    public Unsubscribe listenEvent(Consumer<EventBusEvent> listener) {
        return integrationEventBus.listenEvent(listener);
    }

    @Override
    public Unsubscribe listenEventSync(Consumer<EventBusEvent> listener) {
        return integrationEventBus.listenEventSync(listener);
    }

    @Override
    public <T extends EventBusEvent> Unsubscribe listenEvent(String type, Consumer<T> listener) {
        return integrationEventBus.listenEvent(type, listener);
    }

    @Override
    public <T extends EventBusEvent> Unsubscribe listenEventSync(String type, Consumer<T> listener) {
        return integrationEventBus.listenEventSync(type, listener);
    }

    @Override
    public void mount(Element root, TsWebUnit unit, EventTarget emitter) {
        if (!loaded) {
            mountDescriptorsFiredBeforeLoad.add(new MountDescriptor(root, unit, emitter));
            return;
        }
        integrationMount.mount(root, unit, emitter);
    }
}
