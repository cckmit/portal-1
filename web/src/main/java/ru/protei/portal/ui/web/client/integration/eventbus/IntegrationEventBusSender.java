package ru.protei.portal.ui.web.client.integration.eventbus;

import ru.protei.portal.ui.web.client.model.event.EventBusEvent;

public interface IntegrationEventBusSender {
    void fireEvent(EventBusEvent event);
}
