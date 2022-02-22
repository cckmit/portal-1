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
import ru.protei.portal.core.model.struct.EmployeesBirthdays;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;

/**
 * Сервис управления сотрудниками
 */
public interface EmployeeService {

    Result<List<PersonShortView>> shortViewList(EmployeeQuery query);

    @Privileged(En_Privilege.EMPLOYEE_VIEW)
    Result<SearchResult<EmployeeShortView>> employeeList(AuthToken token, EmployeeQuery query);

    Result<PersonShortView> getDepartmentHead(AuthToken token, Long departmentId);

    Result<List<WorkerEntryShortView>> getWorkerEntryList(AuthToken token, int offset, int limit);

    @Privileged(En_Privilege.EMPLOYEE_VIEW)
    Result<EmployeeShortView> getEmployee(AuthToken token, Long employee);

    @Privileged(En_Privilege.EMPLOYEE_EDIT)
    Result<EmployeeShortView> getEmployeeWithPrivacyInfo(AuthToken token, Long employeeId);

    @Auditable(En_AuditType.EMPLOYEE_CREATE)
    @Privileged(En_Privilege.EMPLOYEE_CREATE)
    Result<Person> createEmployee(AuthToken token, Person person, List<WorkerEntry> workerEntries);

    @Auditable(En_AuditType.EMPLOYEE_MODIFY)
    @Privileged(En_Privilege.EMPLOYEE_EDIT)
    Result<Person> updateEmployee(AuthToken token, Person person, List<WorkerEntry> workerEntries, boolean needToChangeAccount);

    @Auditable(En_AuditType.EMPLOYEE_MODIFY)
    @Privileged(En_Privilege.EMPLOYEE_EDIT)
    Result<Boolean> fireEmployee(AuthToken token, Person person);

    @Auditable(En_AuditType.WORKER_CREATE)
    @Privileged(En_Privilege.EMPLOYEE_CREATE)
    Result<WorkerEntry> createWorkerEntry(AuthToken token, WorkerEntry worker);

    @Auditable(En_AuditType.WORKER_MODIFY)
    @Privileged(En_Privilege.EMPLOYEE_EDIT)
    Result<WorkerEntry> updateWorkerEntry(AuthToken token, WorkerEntry worker);

    @Privileged(En_Privilege.EMPLOYEE_EDIT)
    Result<Person> updateWorkerEntries(AuthToken token, Long personId, List<WorkerEntry> workerEntryList);

    @Privileged(En_Privilege.EMPLOYEE_VIEW)
    Result<EmployeesBirthdays> getEmployeesBirthdays(AuthToken token, Date dateFrom, Date dateUntil);

    Result<Void> notifyAboutBirthdays();

    @Privileged(En_Privilege.EMPLOYEE_VIEW)
    Result<String> getEmployeeRestVacationDays(AuthToken token, Long employeeId);
}