package ru.protei.portal.ui.web.client.integration.eventbus;

import com.google.gwt.core.client.JavaScriptObject;
import ru.protei.portal.ui.web.client.model.connector.ConnectorMessage;
import ru.protei.portal.ui.web.client.model.event.EventBusEvent;
import ru.protei.portal.ui.web.client.model.Unsubscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class IntegrationEventBusImpl implements IntegrationEventBus {

    private final List<Consumer<EventBusEvent>> listeners = new ArrayList<>();;
    private final List<Consumer<EventBusEvent>> listenersSync = new ArrayList<>();;

    public IntegrationEventBusImpl() {
    }

    @Override
    public void fireEvent(EventBusEvent event) {
        ConnectorMessage message = makeEventbusMessage(event);
        send(message);
    }

    private native ConnectorMessage makeEventbusMessage(JavaScriptObject event)/*-{
        return {
            type: "portal-eventbus-event",
            payload: event
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

    @Override
    public Unsubscribe listenEvent(Consumer<EventBusEvent> listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    @Override
    public Unsubscribe listenEventSync(Consumer<EventBusEvent> listener) {
        listenersSync.add(listener);
        return () -> listenersSync.remove(listener);
    }

    @Override
    public <T extends EventBusEvent> Unsubscribe listenEvent(String type, Consumer<T> listener) {
        return listenEvent((event) -> {
            if (!Objects.equals(event.getType(), type)) {
                return;
            }
            listener.accept((T) event);
        });
    }

    @Override
    public <T extends EventBusEvent> Unsubscribe listenEventSync(String type, Consumer<T> listener) {
        return listenEventSync((event) -> {
            if (!Objects.equals(event.getType(), type)) {
                return;
            }
            listener.accept((T) event);
        });
    }

    public void invokeListeners(EventBusEvent event) {
        if (listenersSync.size() > 0) {
            runListeners(listenersSync, event);
        }
        if (listeners.size() > 0) {
            scheduleMacroTask(() -> {
                runListeners(listeners, event);
            });
        }
    }

    private void runListeners(List<Consumer<EventBusEvent>> listeners, EventBusEvent event) {
        for (Consumer<EventBusEvent> listener : listeners) {
            listener.accept(event);
        }
    }

    private native void scheduleMacroTask(Runnable task)/*-{
        $wnd.setTimeout($entry(function () {
            task.@java.lang.Runnable::run()();
        }), 0)
    }-*/;
}
