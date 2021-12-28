package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;

import java.util.Date;
import java.util.List;

public interface WorkerEntryService {
    Result<Void> updateFiredByDate(Date now);

    Result<Void> firePerson(Person person, boolean isFired, Date fireDate, 
                            boolean isDeleted, List<UserLogin> userLogins,
                            boolean isNeedMigrationAtFir);
}
