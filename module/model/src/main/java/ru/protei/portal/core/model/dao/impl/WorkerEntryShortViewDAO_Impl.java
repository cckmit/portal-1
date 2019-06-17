package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.WorkerEntryShortViewDAO;
import ru.protei.portal.core.model.view.WorkerEntryShortView;

import java.util.List;
import java.util.stream.Collectors;

public class WorkerEntryShortViewDAO_Impl extends PortalBaseJdbcDAO<WorkerEntryShortView> implements WorkerEntryShortViewDAO {

    @Override
    public List<WorkerEntryShortView> listByPersonIds(List<Long> personIds) {
        String sql = "personId in (" + personIds.stream().map(id -> "?").collect( Collectors.joining(", ")) + ")";
        return getListByCondition(sql, personIds);
    }
}
