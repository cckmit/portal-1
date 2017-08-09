package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.WorkerPositionDAO;
import ru.protei.portal.core.model.ent.WorkerPosition;

/**
 * Created by turik on 18.08.16.
 */
public class WorkerPositionDAO_Impl extends PortalBaseJdbcDAO<WorkerPosition> implements WorkerPositionDAO {
    @Override
    public WorkerPosition getByExternalId(Long extId, Long companyId) {
        return getByCondition ("pos_extId=? and company_id=?", extId, companyId);
    }
}
