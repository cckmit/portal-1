package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.EmployeesBirthdays;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;

/**
 * Сервис управления сотрудниками
 */
@RemoteServiceRelativePath( "springGwtServices/EmployeeController" )
public interface EmployeeController extends RemoteService {

    /**
     * Получение списка сотрудников
     * @return список сотрудников
     */
    SearchResult<EmployeeShortView> getEmployees(EmployeeQuery query) throws RequestFailedException;

    /**
     * Получение списка сокращенного представления сотрудника
     * @param query запрос
     * @return
     */
    List<PersonShortView> getEmployeeViewList(EmployeeQuery query) throws RequestFailedException;

    /**
     * Получение руководителя подразделения
     * @param departmentId айди подразделения
     * @return сокращенное представление руководителя
     */
    PersonShortView getDepartmentHead(Long departmentId) throws RequestFailedException;

    List<WorkerEntryShortView> getWorkerEntryList(int offset, int limit) throws RequestFailedException;

    EmployeeShortView getEmployee(Long employeeId) throws RequestFailedException;

    EmployeeShortView getEmployeeWithPrivacyInfo(Long employeeId) throws RequestFailedException;

    Person saveEmployee(Person person, List<WorkerEntry> workerEntries, boolean isEditablePerson, boolean needToChangeAccount) throws RequestFailedException;

    boolean fireEmployee(Person person) throws RequestFailedException;

    EmployeesBirthdays getEmployeesBirthdays(Date dateFrom, Date dateUntil) throws RequestFailedException;

    String getEmployeeRestVacationDays(Long employeeId) throws RequestFailedException;

    List<PersonShortView> getAccountingEmployee() throws RequestFailedException;
}
