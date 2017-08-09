package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.core.model.query.DataQuery;

import java.util.List;

/**
 * Created by turik on 19.08.16.
 */
public interface WorkerEntryDAO extends PortalBaseDAO<WorkerEntry> {
    boolean checkExistsByExternalId(Long extId, Long companyId);
    boolean checkExistsByPersonId(Long personId);
    boolean checkExistsByDepId(Long depId);
    WorkerEntry getByExternalId(Long extId, Long companyId);
}
