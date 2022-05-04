package ru.protei.portal.test.api.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.test.api.model.WorkerRecordTestAPI;

public interface WorkerTestApiService {

    Result<WorkerRecordTestAPI> addWorker(WorkerRecordTestAPI workerRecordTestAPI);
}
