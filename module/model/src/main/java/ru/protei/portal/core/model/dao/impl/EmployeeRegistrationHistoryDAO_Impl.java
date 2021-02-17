package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.EmployeeRegistrationHistoryDAO;
import ru.protei.portal.core.model.ent.EmployeeRegistrationHistory;
import ru.protei.portal.core.model.helper.HelperFunc;

import java.util.List;

public class EmployeeRegistrationHistoryDAO_Impl
        extends PortalBaseJdbcDAO<EmployeeRegistrationHistory> implements EmployeeRegistrationHistoryDAO {

    @Override
    public List<EmployeeRegistrationHistory> getListByHistoryIds(List<Long> historyIds) {
        return getListByCondition(
                "employee_registration_history.history_id IN " +
                        HelperFunc.makeInArg(historyIds, false)
        );
    }
}
