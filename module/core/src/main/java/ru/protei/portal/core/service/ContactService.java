package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

/**
 * Сервис управления контактами
 */
public interface ContactService {

    CoreResponse<List<PersonShortView>> shortViewList(ContactQuery query);
    CoreResponse<Long> count(ContactQuery query);
    CoreResponse<List<Person>> contactList(ContactQuery query);
    CoreResponse<Person> getContact(long id);
    CoreResponse<Person> saveContact(Person p);

    CoreResponse<UserLogin> getUserLogin( long id);
    CoreResponse<UserLogin> saveUserLogin(UserLogin userLogin);
    CoreResponse<Boolean> removeUserLogin(UserLogin userLogin);
    CoreResponse<Boolean> checkUniqueUserLoginByLogin(String login, Long excludeId);
}
