package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Асинхронный сервис управления контактами
 */
public interface ContactControllerAsync {

    /**
     * Получение списка контактов компании
     * @param query запрос
     * @return список контактов
     */
    void getContacts( ContactQuery query, AsyncCallback< SearchResult< Person > > async );

    void getContact ( long id, AsyncCallback<Person> callback );

    void saveContact ( Person p, AsyncCallback<Person> callback );

    void fireContact(long id, AsyncCallback<Boolean> async);

    void removeContact(long id, AsyncCallback<Boolean> async);

    /**
     * Получение списка сокращенного представления контакта
     * @param query запрос
     * @param callback
     */
    void getContactViewList( ContactQuery query, AsyncCallback< List<PersonShortView> > callback );

    void saveAccount (UserLogin userLogin, Boolean sendWelcomeEmail, AsyncCallback< Boolean > callback );
}
