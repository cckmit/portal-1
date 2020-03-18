package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.core.model.query.WorkerEntryQuery;

import java.util.List;

/**
 * Created by turik on 19.08.16.
 */
public interface WorkerEntryDAO extends PortalBaseDAO<WorkerEntry> {
    boolean checkExistsByExternalId(String extId, Long companyId);
    boolean checkExistsByPersonId(Long personId);
    boolean checkExistsByDepId(Long depId);
    boolean checkExistsByDep(String extId, Long companyId);
    boolean checkExistsByPosId(Long posId);
    boolean checkExistsByPosName(String name, Long companyId);
    WorkerEntry getByExternalId(String extId, Long companyId);
    List< WorkerEntry > getWorkers(WorkerEntryQuery query);
    List< WorkerEntry > getWorkersByDepartment(Long depId);
    List< WorkerEntry > getPartialWorkersDepartments(List<Long> workerIdList);
}
