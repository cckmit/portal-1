package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Сервис управления контактами
 */
@RemoteServiceRelativePath( "springGwtServices/ContactController" )
public interface ContactController extends RemoteService {

    /**
     * Получение списка контактов компании
     * @param query запрос
     * @return список контактов
     */
    SearchResult< Person > getContacts (ContactQuery query ) throws RequestFailedException;

    Person getContact ( long id ) throws RequestFailedException;

    Person saveContact ( Person p ) throws RequestFailedException;

    boolean fireContact( long id ) throws RequestFailedException;

    Long removeContact(long id ) throws RequestFailedException;

    /**
     * Получение списка сокращенного представления контакта
     * @param query запрос
     * @return
     */
    List<PersonShortView> getContactViewList( ContactQuery query ) throws RequestFailedException;

    Long saveAccount ( UserLogin userLogin, Boolean sendWelcomeEmail ) throws RequestFailedException;
}
