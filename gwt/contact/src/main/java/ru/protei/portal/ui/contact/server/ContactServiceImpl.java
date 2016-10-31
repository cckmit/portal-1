package ru.protei.portal.ui.contact.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.contact.client.service.ContactService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Реализация сервиса по работе с контактами
 */
@Service( "ContactService" )
public class ContactServiceImpl extends RemoteServiceServlet implements ContactService {

    @Override
    public List<Person> getContacts(String searchPattern, Company company, int isFired, En_SortField sortField, Boolean sortDir) throws RequestFailedException {
        List< Person > list = new ArrayList<>();
        Person person = new Person();
        person.setCreated( new Date() );
        person.setFired( false );
        person.setFirstName( "Василий" );
        person.setLastName( "Пупкин" );
        person.setSecondName( "Васильевич" );
        person.setCompanyId( 2L);
        person.setDisplayName( "Пупкин Василий Васильевич");
        person.setDisplayShortName( "Пупкин В.В." );
        person.setPosition( "инженер" );
        person.setMobilePhone( "89996665544" );
        list.add(person);
        return list;
    }

    @Autowired
    ru.protei.portal.core.service.dict.ProductService productService;

}
