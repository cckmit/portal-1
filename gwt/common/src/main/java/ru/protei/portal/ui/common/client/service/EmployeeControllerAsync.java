package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
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

    void createEmployeePerson(Person person, AsyncCallback<Person> async);

    void createEmployeeWorker(WorkerEntry workerEntry, AsyncCallback<WorkerEntry> async);

    void updateEmployeeWorker(WorkerEntry workerEntry, AsyncCallback<Boolean> async);

    void updateEmployeeWorkers(List<WorkerEntry> workerEntryList, AsyncCallback<Boolean> async);

    void getEmployeeWithChangedHiddenCompanyNames(Long employeeId, AsyncCallback<EmployeeShortView> async);

    void getEmployeesWithChangedHiddenCompanyNames(EmployeeQuery query, AsyncCallback<SearchResult<EmployeeShortView>> async);

    void fireEmployee(Person person, AsyncCallback<Boolean> async);

    void updateEmployeePerson(Person person, boolean needToChangeAccount, AsyncCallback<Boolean> async);
}
