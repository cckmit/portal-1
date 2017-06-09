package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления контактами
 */
public class ContactServiceImpl implements ContactService {

    private static Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    @Autowired
    PersonDAO personDAO;

    @Autowired
    UserLoginDAO userLoginDAO;

    @Override
    public CoreResponse<List<PersonShortView>> shortViewList(ContactQuery query) {
        List<Person> list = personDAO.getContacts(query);

        if (list == null)
            new CoreResponse<List<PersonShortView>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<PersonShortView> result = list.stream().map(Person::toPersonShortView).collect(Collectors.toList());

        return new CoreResponse<List<PersonShortView>>().success(result,result.size());
    }

    @Override
    public CoreResponse<List<Person>> contactList(ContactQuery query) {
        List<Person> list = personDAO.getContacts(query);

        if (list == null)
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
            log.warn("person with id = {} is employee",p.getId());
            return new CoreResponse<Person>().error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (HelperFunc.isEmpty(p.getFirstName()) || HelperFunc.isEmpty(p.getLastName())
                || p.getCompanyId() == null)
            return new CoreResponse<Person>().error(En_ResultStatus.VALIDATION_ERROR);

        if (HelperFunc.isEmpty(p.getDisplayName())) {
            p.setDisplayName(p.getLastName() + " " + p.getFirstName());
        }

        if (HelperFunc.isEmpty(p.getDisplayShortName())) {
            StringBuilder b = new StringBuilder();
            b.append (p.getLastName()).append(" ")
                    .append (p.getFirstName().substring(0,1).toUpperCase()).append(".")
            ;

            if (!p.getSecondName().isEmpty()) {
                b.append(" ").append(p.getSecondName().substring(0,1).toUpperCase()).append(".");
            }

            p.setDisplayShortName(b.toString());
        }

        if (p.getCreated() == null)
            p.setCreated(new Date());

        if (p.getCreator() == null)
            p.setCreator("service");

        if (p.getGender() == null)
            p.setGender(En_Gender.UNDEFINED);

        if (personDAO.saveOrUpdate(p)) {
            return new CoreResponse<Person>().success(p);
        }

        return new CoreResponse<Person>().error(En_ResultStatus.INTERNAL_ERROR);
    }


    @Override
    public CoreResponse<Long> count(ContactQuery query) {
        return new CoreResponse<Long>().success(personDAO.count(query));
    }
}
