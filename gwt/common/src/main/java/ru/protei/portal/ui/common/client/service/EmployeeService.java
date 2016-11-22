package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления сотрудниками
 */
@RemoteServiceRelativePath( "springGwtServices/EmployeeService" )
public interface EmployeeService extends RemoteService {

    /**
     * Получение списка сотрудников
     * @return список контактов
     */
    List< Person > getEmployees () throws RequestFailedException;

    /**
     * Получение списка сокращенного представления сотрудника (name,id)
     * @return
     */
    List<EntityOption> getEmployeeOptionList() throws RequestFailedException;

}
