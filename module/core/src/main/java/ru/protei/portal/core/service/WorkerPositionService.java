package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.WorkerPosition;

import java.util.List;

/**
 * Сервис управления отделами
 */
public interface WorkerPositionService {

    Result<List<WorkerPosition>> getWorkerPositions(AuthToken token, Long companyId);

    Result<Long> createWorkerPositions(AuthToken token, WorkerPosition workerPosition);

    Result<Long> updateWorkerPositions(AuthToken token, WorkerPosition workerPosition);

    Result<Long> removeWorkerPositions(AuthToken token, Long workerPositionId);

}
