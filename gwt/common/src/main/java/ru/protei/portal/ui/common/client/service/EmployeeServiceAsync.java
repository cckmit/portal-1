package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Person;

import java.util.List;

/**
 * Сервис управления сотрудниками
 */
public interface EmployeeServiceAsync {
    /**
     * Получение списка сотрудников
     * @return список контактов
     */
    void getEmployees(AsyncCallback<List<Person>> async);

}
