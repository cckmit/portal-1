package ru.protei.portal.ui.web.client.integration.eventbus;

import ru.protei.portal.ui.web.client.model.event.EventBusEvent;
import ru.protei.portal.ui.web.client.model.Unsubscribe;

import java.util.function.Consumer;

public interface IntegrationEventBus {
    void fireEvent(EventBusEvent event);

    Unsubscribe listenEvent(Consumer<EventBusEvent> listener);

    Unsubscribe listenEventSync(Consumer<EventBusEvent> listener);

    <T extends EventBusEvent> Unsubscribe listenEvent(String type, Consumer<T> listener);

    <T extends EventBusEvent> Unsubscribe listenEventSync(String type, Consumer<T> listener);
}
