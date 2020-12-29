package ru.protei.portal.core.service.autoopencase;

import ru.protei.portal.core.event.CaseObjectCreateEvent;

import java.util.concurrent.Future;

public interface AutoOpenCaseService {
    void scheduleCaseOpen();

    void onCaseObjectCreateEvent(CaseObjectCreateEvent event);
    void performCaseOpen(Long caseId);
    Future<?> createTask(Long caseId, long delay);
}
