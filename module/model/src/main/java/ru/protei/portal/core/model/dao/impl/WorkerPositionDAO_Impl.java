package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.WorkerPositionDAO;
import ru.protei.portal.core.model.ent.WorkerPosition;

import java.util.List;

/**
 * Created by turik on 18.08.16.
 */
public class WorkerPositionDAO_Impl extends PortalBaseJdbcDAO<WorkerPosition> implements WorkerPositionDAO {
    @Override
    public WorkerPosition getByName(String name, Long companyId) {
        return getByCondition ("pos_name=? and company_id=?", name, companyId);
    }

    @Override
    public boolean checkExistsByName(String name, Long companyId) {
        return checkExistsByCondition ("pos_name=? and company_id=?", name, companyId);
    }

    @Override
    public boolean checkExistsByNameAndPosId(String name, Long companyId, Long positionId) {
        return checkExistsByCondition ("pos_name=? and company_id=? and id!=?", name, companyId);
    }

    @Override
    public boolean checkExistsByName(String name, Long companyId, Long excludeId) {
        return checkExistsByCondition ("pos_name=? and company_id=? and id != ?", name, companyId, excludeId);
    }

    @Override
    public List<WorkerPosition> getListByCompanyId(Long companyId) {
        return getListByCondition ("company_id=?", companyId);
    }
}
