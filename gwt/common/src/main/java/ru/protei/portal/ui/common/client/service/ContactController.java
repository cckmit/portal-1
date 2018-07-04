package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

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
    List< Person > getContacts ( ContactQuery query ) throws RequestFailedException;

    Person getContact ( long id ) throws RequestFailedException;

    Person saveContact ( Person p ) throws RequestFailedException;

    Long getContactsCount( ContactQuery query ) throws RequestFailedException;

    boolean fireContact( long id ) throws RequestFailedException;

    boolean removeContact( long id ) throws RequestFailedException;

    /**
     * Получение списка сокращенного представления контакта
     * @param query запрос
     * @return
     */
    List<PersonShortView> getContactViewList( ContactQuery query ) throws RequestFailedException;

    boolean saveAccount ( UserLogin userLogin, NotificationEntry notificationEntry ) throws RequestFailedException;
}
