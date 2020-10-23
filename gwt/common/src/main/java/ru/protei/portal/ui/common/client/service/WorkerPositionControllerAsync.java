package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.WorkerPosition;

import java.util.List;

public interface WorkerPositionControllerAsync {
    void getWorkerPositions(Long companyId, AsyncCallback<List<WorkerPosition>> async);

    void removeWorkerPosition(WorkerPosition workerPosition, AsyncCallback<Long> async);

    void createWorkerPosition(WorkerPosition workerPosition, AsyncCallback<Long> async);

    void updateWorkerPosition(WorkerPosition workerPosition, AsyncCallback<Long> async);
}
