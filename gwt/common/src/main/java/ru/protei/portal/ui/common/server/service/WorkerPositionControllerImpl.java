package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.WorkerPosition;
import ru.protei.portal.core.service.WorkerPositionService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.WorkerPositionController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;

@Service("WorkerPositionController")
public class WorkerPositionControllerImpl implements WorkerPositionController {

    @Override
    public List<WorkerPosition> getWorkerPositions(Long companyId) throws RequestFailedException {
        log.info("getWorkerPositions(): companyId={}", companyId);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(workerPositionService.getWorkerPositions(authToken, companyId));
    }

    @Override
    public Boolean removeWorkerPosition(WorkerPosition workerPosition) throws RequestFailedException {
        log.info("removeWorkerPosition(): workerPosition={}", workerPosition);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(workerPositionService.removeWorkerPosition(authToken, workerPosition));
    }

    @Override
    public Long createWorkerPosition(WorkerPosition workerPosition) throws RequestFailedException {
        log.info("createWorkerPosition(): workerPosition={}", workerPosition);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(workerPositionService.createWorkerPosition(authToken, workerPosition));
    }

    @Override
    public Long updateWorkerPosition(WorkerPosition workerPosition) throws RequestFailedException {
        log.info("updateWorkerPosition(): workerPosition={}", workerPosition);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(workerPositionService.updateWorkerPosition(authToken, workerPosition));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    WorkerPositionService workerPositionService;

    private static final Logger log = LoggerFactory.getLogger(WorkerPositionControllerImpl.class);
}
