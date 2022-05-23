package ru.protei.portal.ui.webts.client.integration;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import ru.protei.portal.ui.webts.client.integration.eventbus.IntegrationEventBusListenerImpl;
import ru.protei.portal.ui.webts.client.integration.eventbus.IntegrationEventBusSenderImpl;
import ru.protei.portal.ui.webts.client.integration.mount.IntegrationMountImpl;
import ru.protei.portal.ui.webts.client.model.MountDescriptor;
import ru.protei.portal.ui.webts.client.model.TsWebUnit;
import ru.protei.portal.ui.webts.client.model.Unsubscribe;
import ru.protei.portal.ui.webts.client.model.connector.ConnectorMessage;
import ru.protei.portal.ui.webts.client.model.event.EventBusEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NativeWebIntegrationImpl implements NativeWebIntegration {

    private final IntegrationMountImpl integrationMount;
    private final IntegrationEventBusListenerImpl integrationEventBusListener;
    private final IntegrationEventBusSenderImpl integrationEventBusSender;
    private boolean loaded;
    private final List<EventBusEvent> eventBusEventsFiredBeforeLoad;
    private final List<MountDescriptor> mountDescriptorsFiredBeforeLoad;

    public NativeWebIntegrationImpl() {
        integrationMount = new IntegrationMountImpl();
        integrationEventBusListener = new IntegrationEventBusListenerImpl();
        integrationEventBusSender = new IntegrationEventBusSenderImpl();
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
        if (!$wnd.Protei_PORTAL_Bridge) {
            $wnd.Protei_PORTAL_Bridge = {};
        }
        $wnd.Protei_PORTAL_Bridge.messageOut = $entry(function (message) {
            self.@ru.protei.portal.ui.webts.client.integration.NativeWebIntegrationImpl::onConnectorMessage(*)(message);
        });
    }-*/;

    private void onConnectorMessage(ConnectorMessage message) {
        String type = message.getType();
        if (type == null) {
            return;
        }
        switch (type) {
            case "eventbus-event": {
                EventBusEvent event = message.getPayload().cast();
                integrationEventBusListener.invokeListeners(event);
                break;
            }
            case "mount": {
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
        integrationEventBusSender.fireEvent(event);
    }

    @Override
    public Unsubscribe listenEvent(Consumer<EventBusEvent> listener) {
        return integrationEventBusListener.listenEvent(listener);
    }

    @Override
    public Unsubscribe listenEventSync(Consumer<EventBusEvent> listener) {
        return integrationEventBusListener.listenEventSync(listener);
    }

    @Override
    public <T extends EventBusEvent> Unsubscribe listenEvent(String type, Consumer<T> listener) {
        return integrationEventBusListener.listenEvent(type, listener);
    }

    @Override
    public <T extends EventBusEvent> Unsubscribe listenEventSync(String type, Consumer<T> listener) {
        return integrationEventBusListener.listenEventSync(type, listener);
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
