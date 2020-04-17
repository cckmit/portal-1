package ru.protei.portal.core.service;

import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.core.event.AssembledProjectEvent;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public interface AssemblerProjectService {
    @Async(BACKGROUND_TASKS)
    void proceed(AssembledProjectEvent sourceEvent);
}
