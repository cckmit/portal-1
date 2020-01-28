package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeDetailView;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.WorkerView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления сотрудниками
 */
public interface EmployeeService {

    Result<List<PersonShortView>> shortViewList( EmployeeQuery query);
    Result<List<WorkerView>> list( String param);

    @Privileged(En_Privilege.EMPLOYEE_VIEW)
    Result<SearchResult<EmployeeShortView>> employeeList( AuthToken token, EmployeeQuery query);

    Result<Person> getEmployee( Long id );
    EmployeeDetailView getEmployeeProfile (Long id);
    EmployeeDetailView getEmployeeAbsences(Long id, Long tFrom, Long tTill, Boolean isFull);

    Result<PersonShortView> getEmployeeById( AuthToken token, Long emploeeId );

}
