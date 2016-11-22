package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.ContactShortView;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

/**
 * Сервис управления контактами
 */
public interface ContactService {

    CoreResponse<List<ContactShortView>> contactShortViewList(ContactQuery query);
    CoreResponse<List<Person>> contactList(ContactQuery query);
    CoreResponse<Person> getContact(long id);
    CoreResponse<Person> saveContact(Person p);
}
