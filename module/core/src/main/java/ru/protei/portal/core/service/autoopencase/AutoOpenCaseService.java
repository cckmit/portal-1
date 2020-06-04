package ru.protei.portal.core.service.autoopencase;

import java.util.concurrent.ScheduledFuture;

public interface AutoOpenCaseService {
    void processNewCreatedCaseToAutoOpen(Long caseId, Long companyId);
    ScheduledFuture<?> createTask(Long caseId, long delay);
}
