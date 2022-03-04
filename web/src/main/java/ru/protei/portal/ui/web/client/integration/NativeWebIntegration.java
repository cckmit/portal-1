package ru.protei.portal.ui.web.client.integration;

import ru.protei.portal.ui.web.client.integration.eventbus.IntegrationEventBus;
import ru.protei.portal.ui.web.client.integration.mount.IntegrationMount;

public interface NativeWebIntegration extends IntegrationEventBus, IntegrationMount {
    void setup();
}
