package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.WorkerEntryDAO;
import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.query.WorkerEntryQuery;

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
    public boolean checkExistsByDep(String extId, Long companyId) {
        return checkExistsByCondition ("dep_id=(select id from company_dep cd where cd.dep_extId=? and cd.company_id=?)", extId, companyId);
    }

    @Override
    public boolean checkExistsByPosId(Long posId) {
        return checkExistsByCondition ("positionId=?", posId);
    }

    @Override
    public boolean checkExistsByPosName(String name, Long companyId) {
        return checkExistsByCondition ("positionId=(select id from worker_position wp where wp.pos_name=? and wp.company_id=?)", name, companyId);
    }

    @Override
    public WorkerEntry getByExternalId(String extId, Long companyId) {
        return getByCondition ("worker_entry.worker_extId=? and worker_entry.companyId=?", extId, companyId);
    }

    @Override
    public List< WorkerEntry > getWorkers(WorkerEntryQuery query) {
        return listByQuery(query);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(WorkerEntryQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");
            if (query.getPersonId() != null) {
                condition.append(" and worker_entry.personId = ?");
                args.add(query.getPersonId());
            }
            if (query.getActive() != null) {
                condition.append(" and worker_entry.active = ?");
                args.add(query.getActive());
            }
        });
    }
}
