package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.WorkerEntry;
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
    Result<SearchResult<EmployeeShortView>> employeeListWithChangedHiddenCompanyNames(AuthToken token, EmployeeQuery query);

    @Privileged(En_Privilege.EMPLOYEE_VIEW)
    Result<EmployeeShortView> getEmployee(AuthToken token, Long employeeId);

    Result<PersonShortView> getDepartmentHead(AuthToken token, Long departmentId);

    @Privileged(En_Privilege.EMPLOYEE_VIEW)
    Result<EmployeeShortView> getEmployeeWithChangedHiddenCompanyNames(AuthToken token, Long employee);

    @Auditable(En_AuditType.EMPLOYEE_CREATE)
    @Privileged(En_Privilege.EMPLOYEE_CREATE)
    Result<Person> createEmployeePerson(AuthToken token, Person person);

    @Auditable(En_AuditType.EMPLOYEE_MODIFY)
    @Privileged(En_Privilege.EMPLOYEE_EDIT)
    Result<Boolean> updateEmployeePerson(AuthToken token, Person person, boolean needToChangeAccount);

    @Auditable(En_AuditType.WORKER_CREATE)
    @Privileged(En_Privilege.EMPLOYEE_CREATE)
    Result<WorkerEntry> createEmployeeWorker(AuthToken token, WorkerEntry worker);

    @Auditable(En_AuditType.WORKER_MODIFY)
    @Privileged(En_Privilege.EMPLOYEE_EDIT)
    Result<Boolean> updateEmployeeWorker(AuthToken token, WorkerEntry worker);

    @Auditable(En_AuditType.EMPLOYEE_MODIFY)
    @Privileged(En_Privilege.EMPLOYEE_EDIT)
    Result<Boolean> fireEmployee(AuthToken token, Person person);

    @Privileged(En_Privilege.EMPLOYEE_EDIT)
    Result<Boolean> updateEmployeeWorkers(AuthToken token, List<WorkerEntry> workerEntryList);
}
