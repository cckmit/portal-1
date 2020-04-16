package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.WorkerPosition;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис по работе с компаниями
 */
@RemoteServiceRelativePath( "springGwtServices/WorkerPositionController" )
public interface WorkerPositionController extends RemoteService {

    List<WorkerPosition> getWorkerPositions(Long companyId) throws RequestFailedException;
    
    Long removeWorkerPosition(Long workerPositionId) throws RequestFailedException;

    Long createWorkerPosition (WorkerPosition workerPosition) throws RequestFailedException;

    Long updateWorkerPosition (WorkerPosition workerPosition) throws RequestFailedException;

}
