package ru.protei.portal.core.service;

import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.core.event.AssembledDeliveryEvent;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public interface AssemblerDeliveryService {
    @Async(BACKGROUND_TASKS)
    void proceed(AssembledDeliveryEvent sourceEvent);
}
