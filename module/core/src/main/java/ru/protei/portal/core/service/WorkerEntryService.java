package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;

import java.util.Date;

public interface WorkerEntryService {
    Result<Void> updateFiredByDate(Date now);
}
