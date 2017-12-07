package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.WorkerEntryDAO;
import ru.protei.portal.core.model.ent.WorkerEntry;

import java.util.List;

/**
 * Created by turik on 19.08.16.
 */
public class WorkerEntryDAO_Impl extends PortalBaseJdbcDAO<WorkerEntry> implements WorkerEntryDAO {
    @Override
    public boolean checkExistsByExternalId(String extId, Long companyId) {
        return checkExistsByCondition ("worker_entry.worker_extId=? and worker_entry.companyId=?", extId, companyId);
    }

    @Override
    public boolean checkExistsByPersonId(Long personId) {
        return checkExistsByCondition ("personId=?", personId);
    }

    @Override
    public boolean checkExistsByDepId(Long depId) {
        return checkExistsByCondition ("dep_id=?", depId);
    }

    @Override
    public WorkerEntry getByExternalId(String extId, Long companyId) {
        return getByCondition ("worker_entry.worker_extId=? and worker_entry.companyId=?", extId, companyId);
    }

    @Override
    public boolean checkExistsByPosId(Long posId) {
        return checkExistsByCondition ("positionId=?", posId);
    }
}
