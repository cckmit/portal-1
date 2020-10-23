package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.WorkerEntryDAO;
import ru.protei.portal.core.model.dao.WorkerPositionDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.WorkerPosition;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.service.policy.PolicyService;

import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class WorkerPositionServiceImpl implements WorkerPositionService{

    @Autowired
    WorkerPositionDAO workerPositionDAO;

    @Autowired
    WorkerEntryDAO workerEntryDAO;

    @Autowired
    PolicyService policyService;

    @Override
    public Result<List<WorkerPosition>> getWorkerPositions(AuthToken token, Long companyId) {
        List<WorkerPosition> list = workerPositionDAO.getListByCompanyId(companyId);
        return Result.ok(list);
    }

    @Override
    public Result<Long> createWorkerPosition(AuthToken token, WorkerPosition workerPosition) {
        if (!isValid(workerPosition)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (workerPositionDAO.checkExistsByName(workerPosition.getName(), workerPosition.getCompanyId())){
            return error(En_ResultStatus.POSITION_ALREADY_EXIST);
        }

        Long workerPositionId = workerPositionDAO.persist(workerPosition);

        if (workerPositionId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        return ok(workerPositionId);
    }

    @Override
    public Result<Long> updateWorkerPosition(AuthToken token, WorkerPosition workerPosition) {
        if (!isValid(workerPosition)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (workerPositionDAO.checkExistsByName(workerPosition.getName(), workerPosition.getCompanyId(), workerPosition.getId())){
            return error(En_ResultStatus.POSITION_ALREADY_EXIST);
        }

        boolean result = workerPositionDAO.merge(workerPosition);

        if ( !result )
            return error(En_ResultStatus.NOT_UPDATED);

        return ok(workerPosition.getId());
    }

    @Override
    public Result<Boolean> removeWorkerPosition(AuthToken token, WorkerPosition workerPosition) {

        if(workerEntryDAO.checkExistsByPosId(workerPosition.getId())){
            return error(En_ResultStatus.WORKER_WITH_THIS_POSITION_ALREADY_EXIST);
        }

        return ok(workerPositionDAO.removeByKey(workerPosition.getId()));
    }

    private boolean isValid (WorkerPosition workerPosition){
        return workerPosition.getCompanyId() != null && StringUtils.isNotEmpty(workerPosition.getName());
    }
}
