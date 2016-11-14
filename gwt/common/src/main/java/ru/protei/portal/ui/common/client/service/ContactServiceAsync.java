package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;

import java.util.List;

/**
 * Асинхронный сервис управления контактами
 */
public interface ContactServiceAsync {

    /**
     * Получение списка контактов компании
     * @param query запрос
     * @return список контактов
     */
    void getContacts( ContactQuery query, AsyncCallback< List< Person > > async );

    void getContact (long id, AsyncCallback<Person> callback);

    void saveContact (Person p, AsyncCallback<Person> callback);

    /**
     * Получение списка контактов домашней компании
     * @return список контактов
     */
    void getEmployees(AsyncCallback<List<Person>> async);
}
