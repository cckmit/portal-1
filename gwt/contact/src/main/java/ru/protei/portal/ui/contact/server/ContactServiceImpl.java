package ru.protei.portal.ui.contact.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.AccountService;
import ru.protei.portal.ui.common.client.service.ContactService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с контактами
 */
@Service( "ContactService" )
public class ContactServiceImpl implements ContactService {

    @Override
    public List<Person> getContacts( ContactQuery query ) throws RequestFailedException {

        log.debug( "getContacts(): searchPattern={} | companyId={} | isFired={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getCompanyId(), query.getFired(), query.getSortField(), query.getSortDir() );

        CoreResponse<List<Person>> response = contactService.contactList( getDescriptorAndCheckSession().makeAuthToken(), query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public Person getContact(long id) throws RequestFailedException {
        log.debug("get contact, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Person> response = contactService.getContact( descriptor.makeAuthToken(), id );

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

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Person> response = contactService.saveContact( descriptor.makeAuthToken(), p );

        log.debug("store contact, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("store contact, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long getContactsCount( ContactQuery query ) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug( "getContactsCount(): query={}", query );
        return contactService.count( descriptor.makeAuthToken(), query ).getData();
    }

    @Override
    public boolean fireContact(long id) throws RequestFailedException {
        log.debug("fire contact, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Boolean> response = contactService.fireContact(descriptor.makeAuthToken(), id);

        log.debug("fire contact, id: {} -> {} ", id, response.isError() ? response.getStatus() : (response.getData() ? "" : "not ") + "fired");

        return response.isOk() ? response.getData() : false;
    }

    @Override
    public boolean removeContact(long id) throws RequestFailedException {
        log.debug("remove contact, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Boolean> response = contactService.removeContact(descriptor.makeAuthToken(), id);

        log.debug("remove contact, id: {} -> {} ", id, response.isError() ? response.getStatus() : (response.getData() ? "" : "not ") + "removed");

        return response.isOk() ? response.getData() : false;
    }

    public List<PersonShortView> getContactViewList( ContactQuery query ) throws RequestFailedException {

        log.debug( "getContactViewList(): searchPattern={} | companyId={} | isFired={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getCompanyId(), query.getFired(), query.getSortField(), query.getSortDir() );

        CoreResponse< List<PersonShortView> > result = contactService.shortViewList( getDescriptorAndCheckSession().makeAuthToken(), query );

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public boolean saveAccount( UserLogin userLogin ) throws RequestFailedException {
        if ( userLogin == null ) {
            log.warn( "null account in request" );
            throw new RequestFailedException( En_ResultStatus.INTERNAL_ERROR );
        }

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        if ( HelperFunc.isEmpty( userLogin.getUlogin() ) ) {
            if ( userLogin.getId() == null ) {
                return true;
            }

            log.debug( "remove account, id: {} ", userLogin.getId() );

            CoreResponse< Boolean > response = accountService.removeAccount( descriptor.makeAuthToken(), userLogin.getId() );

            log.debug( "remove account, result: {}", response.isOk() ? "ok" : response.getStatus() );

            if ( response.isOk() ) {
                return response.getData();
            }

            throw new RequestFailedException( response.getStatus() );

        } else {
            log.debug( "store account, id: {} ", HelperFunc.nvl( userLogin.getId(), "new" ) );

            if ( !isLoginUnique( userLogin.getUlogin(), userLogin.getId() ) )
                throw new RequestFailedException ( En_ResultStatus.ALREADY_EXIST );

            CoreResponse< UserLogin > response = accountService.saveContactAccount( descriptor.makeAuthToken(), userLogin );

            log.debug( "store account, result: {}", response.isOk() ? "ok" : response.getStatus() );

            if ( response.isOk() ) {
                log.debug( "store account, applied id: {}", response.getData().getId() );
                return true;
            }

            throw new RequestFailedException( response.getStatus() );
        }
    }

    private boolean isLoginUnique( String login, Long excludeId ) throws RequestFailedException {

        log.debug( "isLoginUnique(): login={}, excludeId={}", login, excludeId );

        CoreResponse< Boolean > response = accountService.checkUniqueLogin( login, excludeId );

        log.debug( "isLoginUnique() -> {}, {}", response.getStatus(), response.getData() != null ? response.getData() : null );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        log.info( "userSessionDescriptor={}", descriptor );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
    }

    @Autowired
    ru.protei.portal.core.service.ContactService contactService;

    @Autowired
    AccountService accountService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}
