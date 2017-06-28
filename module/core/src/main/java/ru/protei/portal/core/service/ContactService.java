package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;
import java.util.Set;

/**
 * Сервис управления контактами
 */
public interface ContactService {

    CoreResponse<List<PersonShortView>> shortViewList(ContactQuery query);
    CoreResponse<Long> count( ContactQuery query, Set< UserRole > roles );
    CoreResponse<List<Person>> contactList(ContactQuery query);
    CoreResponse<Person> getContact( long id, Set< UserRole > roles );
    CoreResponse<Person> saveContact( Person p, Set< UserRole > roles );
}
