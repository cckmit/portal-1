package ru.protei.portal.core.service.dict;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;

import java.util.List;

/**
 * Created by turik on 01.11.16.
 */
public interface ContactService {

    CoreResponse<List<Person>> contactList(ContactQuery query);
    CoreResponse<Person> getContact (long id);
    CoreResponse<Person> saveContact (Person p);
}
