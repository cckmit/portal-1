package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.winter.core.utils.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Реализация сервиса управления контактами
 */
public class ContactServiceImpl implements ContactService {

    @Autowired
    PersonDAO personDAO;

    @Override
    public CoreResponse<List<Person>> contactList(ContactQuery query) {
        List<Person> list = personDAO.getContactsByQuery(query);

        if ( list == null )
            new CoreResponse<List<Person>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<Person>>().success(list);
    }

    @Override
    public CoreResponse<Person> getContact(long id) {
        Person person = personDAO.getContact(id);

        return person != null ? new CoreResponse<Person>().success(person)
                : new CoreResponse<Person>().error(En_ResultStatus.NOT_FOUND);
    }


    @Override
    public CoreResponse<Person> saveContact(Person p) {
        if (personDAO.isEmployee(p)) {
            return new CoreResponse<Person>().error("This person is employee", "WrongPersonType");
        }

        if (HelperFunc.isEmpty(p.getFirstName()) || HelperFunc.isEmpty(p.getLastName())
                || HelperFunc.isEmpty(p.getDisplayName())
                || p.getCompanyId() == null)
            return new CoreResponse<Person>().error("Enter main contact information", "EmptyContactName");

//        if (HelperFunc.isEmpty(p.getPosition()))
//            return new CoreResponse<Person>().error("Enter main contact information", "EmptyContactPosition");

        if (p.getCreated() == null)
            p.setCreated(new Date());

        if (p.getCreator() == null)
            p.setCreator("service");

        if (p.getGender() == null)
            p.setGender(En_Gender.UNDEFINED);

        if (personDAO.saveOrUpdate(p)) {
            return new CoreResponse<Person>().success(p);
        }

        return new CoreResponse<Person>().error("Unable to store contact", "InternalError");
    }
}
