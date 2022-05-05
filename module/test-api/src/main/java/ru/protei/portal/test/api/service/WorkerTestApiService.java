package ru.protei.portal.test.api.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.WorkerEntry;

public interface WorkerTestApiService {
    Result<En_ResultStatus> addWorker(Person person, WorkerEntry workerEntry, UserLogin userLogin);
}
