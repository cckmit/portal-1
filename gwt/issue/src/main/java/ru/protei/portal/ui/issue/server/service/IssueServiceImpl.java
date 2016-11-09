package ru.protei.portal.ui.issue.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.contact.client.service.ContactService;

import java.util.List;

/**
 * Реализация сервиса по работе с обращениями
 */
@Service( "IssueService" )
public class IssueServiceImpl implements ContactService {

    @Override
    public List<CaseObject> getContacts(ContactQuery contactQuery) throws RequestFailedException {

        log.debug( "getContacts(): searchPattern={} | companyId={} | isFired={} | sortField={} | sortDir={}",
                contactQuery.getSearchString(), contactQuery.getCompanyId(), contactQuery.getFired(), contactQuery.getSortField(), contactQuery.getSortDir() );

        CoreResponse<List<Person>> response = contactService.contactList(contactQuery);

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }


    @Override
    public Person getContact(long id) throws RequestFailedException {
        log.debug("get contact, id: " + id);

        CoreResponse<Person> response = contactService.getContact(id);

        log.debug("get contact, id: " + id + " -> " + (response.isError() ? "error" : ("ok, " + response.getData().getDisplayName())));

        return response.getData();
    }

    @Override
    public Person saveContact(Person p) throws RequestFailedException {
        if (p == null) {
            log.warn("null person in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.debug("store contact, id: " + HelperFunc.nvl(p.getId(), "new"));

        CoreResponse<Person> response = contactService.saveContact(p);

        log.debug("store contact, result: " + (response.isOk() ? "ok" : response.getStatus()));

        if (response.isOk()) {
            log.debug("store contact, applied id: " + response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Autowired
    ru.protei.portal.core.service.ContactService contactService;

    private static final Logger log = LoggerFactory.getLogger( "web" );

}
