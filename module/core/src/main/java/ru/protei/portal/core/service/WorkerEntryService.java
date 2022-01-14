package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;

import java.util.Date;
import java.util.List;

public interface WorkerEntryService {
    Result<Void> updateFiredByDate(Date now);

    Result<Void> updatePositionByDate(Date now);

    Result<Void> firePerson(Person person, Boolean isFired, Date fireDate,
                            Boolean isDeleted, List<UserLogin> userLogins,
                            Boolean isNeedMigrationAtFir);
}
