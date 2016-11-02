package ru.protei.portal.core.service.dict;

import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.ContactQuery;

/**
 * Created by turik on 01.11.16.
 */
public interface ContactService {

    HttpListResult<Person> contactList(ContactQuery query);
}
