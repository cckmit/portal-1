package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;

import java.util.List;

/**
 * Created by turik on 01.11.16.
 */
public class ContactServiceImpl implements ContactService {

    @Autowired
    PersonDAO personDAO;

    @Override
    public CoreResponse<List<Person>> contactList(ContactQuery query) {
        return new CoreResponse<List<Person>>().success(personDAO.getContactsByQuery(query));
    }

    @Override
    public CoreResponse<Person> getContact(long id) {
        Person person = personDAO.getContact(id);

        return person != null ? new CoreResponse<Person>().success(person)
                : new CoreResponse<Person>().error("contact not found", "NE");
    }
}
