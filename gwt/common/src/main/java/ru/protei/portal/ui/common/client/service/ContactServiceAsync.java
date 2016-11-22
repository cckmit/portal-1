package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.EntityOption;

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

    void getContact ( long id, AsyncCallback<Person> callback );

    void saveContact ( Person p, AsyncCallback<Person> callback );

    /**
     * Получение списка сокращенного представления контакта (name,id)
     * @param companyId идентификатор компании
     * @param callback
     */
    void getContactOptionList( long companyId, AsyncCallback< List< EntityOption > > callback );
}
