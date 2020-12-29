package ru.protei.portal.core.service.autoopencase;

import java.util.concurrent.Future;

public interface AutoOpenCaseService {
    void scheduleCaseOpen();

    void processNewCreatedCaseToAutoOpen( Long caseId, Long companyId);
    void performCaseOpen(Long caseId);
    Future<?> createTask(Long caseId, long delay);
}
