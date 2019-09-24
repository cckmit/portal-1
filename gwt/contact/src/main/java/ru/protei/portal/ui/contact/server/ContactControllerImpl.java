package ru.protei.portal.ui.contact.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.AccountService;
import ru.protei.portal.core.service.ContactService;
import ru.protei.portal.ui.common.client.service.ContactController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

/**
 * Реализация сервиса по работе с контактами
 */
@Service( "ContactController" )
public class ContactControllerImpl implements ContactController {

    @Override
    public SearchResult<Person> getContacts( ContactQuery query ) throws RequestFailedException {

        log.info( "getContacts(): searchPattern={} | companyId={} | isFired={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getCompanyId(), query.getFired(), query.getSortField(), query.getSortDir() );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(contactService.getContactsSearchResult(token, query));
    }

    @Override
    public Person getContact(long id) throws RequestFailedException {
        log.info("get contact, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<Person> response = contactService.getContact( descriptor.makeAuthToken(), id );

        log.info("get contact, id: {} -> {} ", id, response.isError() ? "error" : response.getData().getDisplayName());

        return response.getData();
    }

    @Override
    public Person saveContact(Person p) throws RequestFailedException {
        if (p == null) {
            log.warn("null person in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.info("store contact, id: {} ", HelperFunc.nvl(p.getId(), "new"));

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<Person> response = contactService.saveContact( descriptor.makeAuthToken(), p );

        log.info("store contact, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.info("store contact, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public boolean fireContact(long id) throws RequestFailedException {
        log.info("fire contact, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<Boolean> response = contactService.fireContact(descriptor.makeAuthToken(), id);

        log.info("fire contact, id: {} -> {} ", id, response.isError() ? response.getStatus() : (response.getData() ? "" : "not ") + "fired");

        return response.isOk() ? response.getData() : false;
    }

    @Override
    public boolean removeContact(long id) throws RequestFailedException {
        log.info("remove contact, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        Result<Boolean> response = contactService.removeContact(descriptor.makeAuthToken(), id);

        log.info("remove contact, id: {} -> {} ", id, response.isError() ? response.getStatus() : (response.getData() ? "" : "not ") + "removed");

        return response.isOk() ? response.getData() : false;
    }

    public List<PersonShortView> getContactViewList( ContactQuery query ) throws RequestFailedException {

        log.info( "getContactViewList(): searchPattern={} | companyId={} | isFired={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getCompanyId(), query.getFired(), query.getSortField(), query.getSortDir() );

        Result< List<PersonShortView> > result = contactService.shortViewList( getDescriptorAndCheckSession().makeAuthToken(), query );

        log.info( "result status: {}, data-amount: {}", result.getStatus(), size(result.getData()) );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public boolean saveAccount( UserLogin userLogin, Boolean sendWelcomeEmail ) throws RequestFailedException {
        if ( userLogin == null ) {
            log.warn( "null account in request" );
            throw new RequestFailedException( En_ResultStatus.INTERNAL_ERROR );
        }

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        if ( HelperFunc.isEmpty( userLogin.getUlogin() ) ) {
            if ( userLogin.getId() == null ) {
                return true;
            }

            log.info( "remove account, id: {} ", userLogin.getId() );

            Result< Boolean > response = accountService.removeAccount( descriptor.makeAuthToken(), userLogin.getId() );

            log.info( "remove account, result: {}", response.isOk() ? "ok" : response.getStatus() );

            if ( response.isOk() ) {
                return response.getData();
            }

            throw new RequestFailedException( response.getStatus() );

        } else {
            log.info( "store account, id: {} ", HelperFunc.nvl( userLogin.getId(), "new" ) );

            if ( !isLoginUnique( userLogin.getUlogin(), userLogin.getId() ) )
                throw new RequestFailedException ( En_ResultStatus.ALREADY_EXIST );

            Result< UserLogin > response = accountService.saveContactAccount( descriptor.makeAuthToken(), userLogin, sendWelcomeEmail );

            log.info( "store account, result: {}", response.isOk() ? "ok" : response.getStatus() );

            if ( response.isOk() ) {
                log.info( "store account, applied id: {}", response.getData().getId() );
                return true;
            }

            throw new RequestFailedException( response.getStatus() );
        }
    }

    private boolean isLoginUnique( String login, Long excludeId ) throws RequestFailedException {

        log.info( "isLoginUnique(): login={}, excludeId={}", login, excludeId );

        Result< Boolean > response = accountService.checkUniqueLogin( login, excludeId );

        log.info( "isLoginUnique() -> {}, {}", response.getStatus(), response.getData() != null ? response.getData() : null );

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
    ContactService contactService;

    @Autowired
    AccountService accountService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(ContactControllerImpl.class);
}
