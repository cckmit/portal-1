package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.MarkedResult;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

/**
 * Сервис управления сотрудниками
 */
public interface EmployeeControllerAsync {
    /**
     * Получение списка сотрудников
     * @return список контактов
     */
    void getEmployees( EmployeeQuery query, long marker, AsyncCallback< MarkedResult< List< EmployeeShortView > > > async );

    /**
     * Получение списка сокращенного представления сотрудника
     * @param query запрос
     * @param callback
     */
    void getEmployeeViewList( EmployeeQuery query, AsyncCallback< List< PersonShortView > > callback );
}
