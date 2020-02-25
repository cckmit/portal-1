package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления сотрудниками
 */
@RemoteServiceRelativePath( "springGwtServices/EmployeeController" )
public interface EmployeeController extends RemoteService {

    PersonShortView getEmployee(Long employeeId) throws RequestFailedException;

    /**
     * Получение списка сотрудников
     * @return список сотрудников
     */
    SearchResult< EmployeeShortView > getEmployees (EmployeeQuery query ) throws RequestFailedException;

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

    EmployeeShortView getEmployeeShortView( Long employeeId) throws RequestFailedException;
}
