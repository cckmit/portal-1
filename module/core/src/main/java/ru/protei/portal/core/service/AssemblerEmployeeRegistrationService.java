package ru.protei.portal.core.service;

import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.core.event.AssembledEmployeeRegistrationEvent;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public interface AssemblerEmployeeRegistrationService {
    @Async(BACKGROUND_TASKS)
    void proceed(AssembledEmployeeRegistrationEvent sourceEvent);
}
