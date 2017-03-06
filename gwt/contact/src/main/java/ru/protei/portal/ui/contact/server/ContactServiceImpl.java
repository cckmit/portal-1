package ru.protei.portal.ui.contact.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.service.ContactService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Реализация сервиса по работе с контактами
 */
@Service( "ContactService" )
public class ContactServiceImpl implements ContactService {

    @Override
    public List<Person> getContacts( ContactQuery query ) throws RequestFailedException {

        log.debug( "getEquipments(): searchPattern={} | companyId={} | isFired={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getCompanyId(), query.getFired(), query.getSortField(), query.getSortDir() );

        CoreResponse<List<Person>> response = contactService.contactList( query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public Person getContact(long id) throws RequestFailedException {
        log.debug("get contact, id: {}", id);

        CoreResponse<Person> response = contactService.getContact(id);

        log.debug("get contact, id: {} -> {} ", id, response.isError() ? "error" : response.getData().getDisplayName());

        return response.getData();
    }

    @Override
    public Person saveContact(Person p) throws RequestFailedException {
        if (p == null) {
            log.warn("null person in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.debug("store contact, id: {} ", HelperFunc.nvl(p.getId(), "new"));

        CoreResponse<Person> response = contactService.saveContact(p);

        log.debug("store contact, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("store contact, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long getContactsCount( ContactQuery query ) throws RequestFailedException {
        log.debug( "getEquipmentCount(): query={}", query );
        return contactService.count( query ).getData();
    }

    public List<PersonShortView> getContactViewList( ContactQuery query ) throws RequestFailedException {

        log.debug( "getContactViewList(): searchPattern={} | companyId={} | isFired={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getCompanyId(), query.getFired(), query.getSortField(), query.getSortDir() );

        CoreResponse< List<PersonShortView> > result = contactService.shortViewList( query );

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Autowired
    ru.protei.portal.core.service.ContactService contactService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
