package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface EmployeeShortViewDAO extends PortalBaseDAO<EmployeeShortView> {

    List<EmployeeShortView> getEmployees(EmployeeQuery query);

    @SqlConditionBuilder
    SqlCondition createEmployeeSqlCondition(EmployeeQuery query);

    SearchResult<EmployeeShortView> getSearchResult(EmployeeQuery query);
}
