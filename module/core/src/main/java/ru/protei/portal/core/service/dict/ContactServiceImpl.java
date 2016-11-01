package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.BaseQuery;

/**
 * Created by turik on 01.11.16.
 */
public class ContactServiceImpl implements ContactService {

    @Autowired
    PersonDAO personDAO;

    @Override
    public HttpListResult<Person> contactList(BaseQuery query) {
        return null;
    }
}
