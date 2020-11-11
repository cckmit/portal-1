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
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.AccountService;
import ru.protei.portal.core.service.ContactService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.ContactController;
import ru.protei.portal.ui.common.server.ServiceUtils;
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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Person> response = contactService.getContact( token, id );

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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Person> response = contactService.saveContact( token, p );

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

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Boolean> response = contactService.fireContact(token, id);

        log.info("fire contact, id: {} -> {} ", id, response.isError() ? response.getStatus() : (response.getData() ? "" : "not ") + "fired");

        return response.isOk() ? response.getData() : false;
    }

    @Override
    public Long removeContact(long id) throws RequestFailedException {
        log.info("remove contact, id: {}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Long> response = contactService.removeContact(token, id);

        if ( response.isError() ) {
            log.info("contact was not removed, status={} ", response.getStatus());
            throw new RequestFailedException( response.getStatus() );
        }

        return response.getData();
    }

    public List<PersonShortView> getContactViewList( ContactQuery query ) throws RequestFailedException {

        log.info( "getContactViewList(): searchPattern={} | companyId={} | isFired={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getCompanyId(), query.getFired(), query.getSortField(), query.getSortDir() );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result< List<PersonShortView> > result = contactService.shortViewList( token, query );

        log.info( "result status: {}, data-amount: {}", result.getStatus(), size(result.getData()) );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public Long saveAccount( UserLogin userLogin, Boolean sendWelcomeEmail ) throws RequestFailedException {
        if ( userLogin == null ) {
            log.warn( "null account in request" );
            throw new RequestFailedException( En_ResultStatus.INTERNAL_ERROR );
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        if ( HelperFunc.isEmpty( userLogin.getUlogin() ) ) {
            if ( userLogin.getId() == null ) {
                return userLogin.getId();
            }

            log.info( "remove account, id: {} ", userLogin.getId() );

            Result< Long > response = accountService.removeAccount( token, userLogin.getId() );

            log.info( "remove account, result: {}", response.isOk() ? "ok" : response.getStatus() );

            if ( response.isOk() ) {
                return response.getData();
            }

            throw new RequestFailedException( response.getStatus() );

        } else {
            log.info( "store account, id: {} ", HelperFunc.nvl( userLogin.getId(), "new" ) );

            if ( !isLoginUnique( userLogin.getUlogin(), userLogin.getId() ) )
                throw new RequestFailedException ( En_ResultStatus.ALREADY_EXIST );

            Result< UserLogin > response = accountService.saveContactAccount( token, userLogin, sendWelcomeEmail );

            log.info( "store account, result: {}", response.isOk() ? "ok" : response.getStatus() );

            if ( response.isOk() ) {
                UserLogin responseUserLogin = response.getData();

                log.info( "store account, applied id: {}", responseUserLogin.getId() );
                return responseUserLogin.getId();
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
