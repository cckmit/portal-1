package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления сотрудниками
 */
public interface EmployeeService {

    @Privileged(En_Privilege.EMPLOYEE_VIEW)
    Result<SearchResult<EmployeeShortView>> employeeList(AuthToken token, EmployeeQuery query);

    Result<List<PersonShortView>> shortViewList(EmployeeQuery query);

    @Privileged(En_Privilege.EMPLOYEE_VIEW)
    Result<EmployeeShortView> getEmployee(AuthToken token, Long employee);

    Result<PersonShortView> getDepartmentHead(AuthToken token, Long departmentId);
}
