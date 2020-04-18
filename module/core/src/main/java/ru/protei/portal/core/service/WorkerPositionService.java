package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.WorkerPosition;

import java.util.List;

/**
 * Сервис управления отделами
 */
public interface WorkerPositionService {

    @Privileged(requireAny = {En_Privilege.EMPLOYEE_CREATE, En_Privilege.EMPLOYEE_EDIT})
    Result<List<WorkerPosition>> getWorkerPositions(AuthToken token, Long companyId);

    @Auditable(En_AuditType.POSITION_CREATE)
    @Privileged(requireAny = {En_Privilege.EMPLOYEE_CREATE})
    Result<Long> createWorkerPositions(AuthToken token, WorkerPosition workerPosition);

    @Auditable(En_AuditType.POSITION_MODIFY)
    @Privileged(requireAny = {En_Privilege.EMPLOYEE_CREATE, En_Privilege.EMPLOYEE_EDIT})
    Result<Long> updateWorkerPositions(AuthToken token, WorkerPosition workerPosition);

    @Auditable(En_AuditType.POSITION_REMOVE)
    @Privileged(requireAny = {En_Privilege.EMPLOYEE_CREATE, En_Privilege.EMPLOYEE_EDIT})
    Result<Long> removeWorkerPositions(AuthToken token, WorkerPosition workerPosition);

}
