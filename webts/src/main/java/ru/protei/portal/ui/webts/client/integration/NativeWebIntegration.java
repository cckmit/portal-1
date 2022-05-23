package ru.protei.portal.ui.webts.client.integration;

import ru.protei.portal.ui.webts.client.integration.eventbus.IntegrationEventBusListener;
import ru.protei.portal.ui.webts.client.integration.eventbus.IntegrationEventBusSender;
import ru.protei.portal.ui.webts.client.integration.mount.IntegrationMount;

public interface NativeWebIntegration extends IntegrationEventBusListener, IntegrationEventBusSender, IntegrationMount {
    void setup();
}
