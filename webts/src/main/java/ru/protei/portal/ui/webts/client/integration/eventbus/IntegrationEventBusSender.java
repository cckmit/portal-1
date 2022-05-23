package ru.protei.portal.ui.webts.client.integration.eventbus;

import ru.protei.portal.ui.webts.client.model.event.EventBusEvent;

public interface IntegrationEventBusSender {
    void fireEvent(EventBusEvent event);
}
