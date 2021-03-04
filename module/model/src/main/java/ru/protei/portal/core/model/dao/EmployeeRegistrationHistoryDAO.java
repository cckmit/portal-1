package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.EmployeeRegistrationHistory;
import ru.protei.portal.core.model.ent.History;

import java.util.List;

public interface EmployeeRegistrationHistoryDAO extends PortalBaseDAO<EmployeeRegistrationHistory> {
    List<EmployeeRegistrationHistory> getListByHistoryIds(List<Long> historyIds);
}
