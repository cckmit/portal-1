package ru.protei.portal.core.service.autoopencase;

import java.util.Random;
import java.util.concurrent.ScheduledFuture;

public interface AutoOpenCaseService {
    void processNewCreatedCaseToAutoOpen(Long caseId, Long companyId);
    ScheduledFuture<?> createTask(Long caseId, Random random, long timeoutOffset);
}
