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
    public List<Person> getContacts(String searchPattern, Company company, Boolean fired, En_SortField sortField, Boolean sortDir) throws RequestFailedException {
        
        log.debug( "getContacts(): searchPattern={} | company={} | isFired={} | sortField={} | sortDir={}",
                searchPattern, company, fired, sortField, (sortDir ? En_SortDir.ASC : En_SortDir.DESC) );
        
        ContactQuery query = new ContactQuery(company, searchPattern, sortField, sortDir ? En_SortDir.ASC : En_SortDir.DESC);
        query.setFired(fired);
        
        CoreResponse<List<Person>> response = contactService.contactList(query);

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Autowired
    ru.protei.portal.core.service.ContactService contactService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
