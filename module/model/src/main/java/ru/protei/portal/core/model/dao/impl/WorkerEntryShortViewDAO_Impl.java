package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.WorkerEntryShortViewDAO;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.WorkerEntryShortView;

import java.util.List;
import java.util.Set;

public class WorkerEntryShortViewDAO_Impl extends PortalBaseJdbcDAO<WorkerEntryShortView> implements WorkerEntryShortViewDAO {

    @Override
    public List<WorkerEntryShortView> listByPersonIds(Set<Long> personIds) {
        String sql = "personId in " + HelperFunc.makeInArg(personIds, false);
        return getListByCondition(sql);
    }
}
