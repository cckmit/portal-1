package ru.protei.portal.core.service.autoopencase;

import java.util.concurrent.Future;

public interface AutoOpenCaseService {
    void scheduleCaseOpen();

    void processNewCreatedCaseToAutoOpen( Long caseId, Long companyId);
    Future<?> createTask(Long caseId, long delay);
}
