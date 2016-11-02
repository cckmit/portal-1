package ru.protei.portal.ui.contact.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.contact.client.service.ContactService;

import java.util.List;

/**
 * Реализация сервиса по работе с контактами
 */
@Service( "ContactService" )
public class ContactServiceImpl implements ContactService {

    @Override
    public List<Person> getContacts(String searchPattern, Company company, int isFired, En_SortField sortField, Boolean sortDir) throws RequestFailedException {
        ContactQuery query = new ContactQuery(company, searchPattern, sortField, sortDir ? En_SortDir.ASC : En_SortDir.DESC);

//        log.debug("before get contact list");

        CoreResponse<List<Person>> response = contactService.contactList(query);

//        log.debug("after get contact list");

        if (response.isError()) {
            throw new RequestFailedException();
        }
//        List< Person > list = new ArrayList<>();
//        Person person = new Person();
//        person.setCreated( new Date() );
//        person.setFired( false );
//        person.setFirstName( "Василий" );
//        person.setLastName( "Пупкин" );
//        person.setSecondName( "Васильевич" );
//        person.setCompanyId( 2L);
//        person.setDisplayName( "Пупкин Василий Васильевич");
//        person.setDisplayShortName( "Пупкин В.В." );
//        person.setPosition( "инженер" );
//        person.setMobilePhone( "89996665544" );
//        list.add(person);
        return response.getData();
    }


    @Override
    public Person getContact(long id) throws RequestFailedException {
        log.debug("get contact, id: " + id);

        CoreResponse<Person> response = contactService.getContact(id);

        log.debug("get contact, id: " + id + " -> " + (response.isError() ? "error" : ("ok, " + response.getData().getDisplayName())));

        return response.getData();
    }

    @Autowired
    ru.protei.portal.core.service.dict.ContactService contactService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
