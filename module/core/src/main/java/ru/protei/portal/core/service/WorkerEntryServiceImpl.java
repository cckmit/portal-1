package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.WorkerEntryDAO;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.WorkerEntry;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.ok;

public class WorkerEntryServiceImpl implements WorkerEntryService{

    @Autowired
    WorkerEntryDAO workerEntryDAO;

    @Autowired
    PersonDAO personDAO;

    @Override
    @Transactional
    public Result<Void> updateFiredByDate(Date now) {
        List<WorkerEntry> entryForFire = workerEntryDAO.getForFireByDate(now);
        entryForFire.forEach(entry -> {
            workerEntryDAO.remove(entry);
            if (!workerEntryDAO.checkExistsByPersonId(entry.getPersonId())) {
                Person person = new Person(entry.getPersonId());
                person.setFired(true, entry.getFiredDate());
                personDAO.partialMerge(person, Person.Columns.IS_FIRED, Person.Columns.FIRE_DATE);
            }
        });
        return ok();
    }
}
