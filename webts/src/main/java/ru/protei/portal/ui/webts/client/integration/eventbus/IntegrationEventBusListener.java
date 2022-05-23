package ru.protei.portal.ui.webts.client.integration.eventbus;

import ru.protei.portal.ui.webts.client.model.Unsubscribe;
import ru.protei.portal.ui.webts.client.model.event.EventBusEvent;

import java.util.function.Consumer;

public interface IntegrationEventBusListener {
    Unsubscribe listenEvent(Consumer<EventBusEvent> listener);

    Unsubscribe listenEventSync(Consumer<EventBusEvent> listener);

    <T extends EventBusEvent> Unsubscribe listenEvent(String type, Consumer<T> listener);

    <T extends EventBusEvent> Unsubscribe listenEventSync(String type, Consumer<T> listener);
}
