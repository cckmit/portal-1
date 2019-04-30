package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.MarkedResult;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

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
    MarkedResult< List< EmployeeShortView > > getEmployees ( EmployeeQuery query, long marker ) throws RequestFailedException;

    /**
     * Получение списка сокращенного представления сотрудника
     * @param query запрос
     * @return
     */
    List< PersonShortView > getEmployeeViewList( EmployeeQuery query ) throws RequestFailedException;
}
