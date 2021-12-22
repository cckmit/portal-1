package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
public interface EmployeeControllerAsync {

    /**
     * Получение списка сотрудников
     * @return список контактов
     */
    void getEmployees(EmployeeQuery query, AsyncCallback<SearchResult< EmployeeShortView >> async);

    /**
     * Получение списка сокращенного представления сотрудника
     * @param query запрос
     * @param callback
     */
    void getEmployeeViewList(EmployeeQuery query, AsyncCallback<List<PersonShortView>> callback);

    void getEmployee(Long employeeId, AsyncCallback<EmployeeShortView> async);

    /**
     * Получение руководителя подразделения
     * @param departmentId айди подразделения
     * @return сокращенное представление руководителя
     */
    void getDepartmentHead(Long departmentId, AsyncCallback<PersonShortView> async);

    void getWorkerEntryList(int offset, int limit, AsyncCallback<List<WorkerEntryShortView>> async);

    void getEmployeeWithChangedHiddenCompanyNames(Long employeeId, AsyncCallback<EmployeeShortView> async);

    void getEmployeesWithChangedHiddenCompanyNames(EmployeeQuery query, AsyncCallback<SearchResult<EmployeeShortView>> async);

    void fireEmployee(Person person, AsyncCallback<Boolean> async);

    void saveEmployee(Person person, List<WorkerEntry> workerEntries, boolean isEditablePerson, boolean needToChangeAccount, AsyncCallback<Person> async);

    void getEmployeesBirthdays(Date dateFrom, Date dateUntil, AsyncCallback<EmployeesBirthdays> async);

    void getEmployeeRestVacationDays(List<String> workerExtIds, AsyncCallback<String> async);
}
