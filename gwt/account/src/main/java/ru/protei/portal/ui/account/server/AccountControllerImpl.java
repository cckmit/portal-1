package ru.protei.portal.ui.account.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.service.AccountService;
import ru.protei.portal.ui.common.client.service.AccountController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с учетными записями
 */
@Service( "AccountController" )
public class AccountControllerImpl implements AccountController {

    @Override
    public List< UserLogin > getAccounts( AccountQuery query ) throws RequestFailedException {

        log.debug( "getAccounts(): query={}", query);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< List< UserLogin > > response = accountService.accountList( descriptor.makeAuthToken(), query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public UserLogin getAccount( long id ) throws RequestFailedException {
        log.debug( "getAccount(): id={}", id );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< UserLogin > response = accountService.getAccount( descriptor.makeAuthToken(), id );

        log.debug( "getAccount(): id={} -> {} ", id, response.isError() ? "error" : response.getData().getUlogin() );

        return response.getData();
    }

    @Override
    public UserLogin getContactAccount(long personId ) throws RequestFailedException {
        log.debug( "getContactAccount(): personId={}", personId );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< UserLogin > response = accountService.getContactAccount( descriptor.makeAuthToken(), personId );

        log.debug( "getContactAccount(): personId={} -> {} ", personId, response.isError() ? "error" : response.getData().getUlogin() );

        return response.getData();
    }

    @Override
    public UserLogin saveAccount( UserLogin userLogin, Boolean sendWelcomeEmail ) throws RequestFailedException {
        log.debug( "saveAccount(): account={} ", userLogin );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        if ( userLogin == null ) {
            throw new RequestFailedException( En_ResultStatus.INTERNAL_ERROR );
        }

        if ( !isLoginUnique( userLogin.getUlogin(), userLogin.getId() ) )
            throw new RequestFailedException ( En_ResultStatus.ALREADY_EXIST );

        CoreResponse< UserLogin > response = accountService.saveAccount( descriptor.makeAuthToken(), userLogin, sendWelcomeEmail );

        log.debug( "saveAccount(): result={}", response.isOk() ? "ok" : response.getStatus() );

        if ( response.isOk() ) {
            log.debug( "saveAccount(): applied id={}", response.getData().getId() );
            return response.getData();
        }

        throw new RequestFailedException( response.getStatus() );
    }

    @Override
    public Long getAccountsCount( AccountQuery query ) throws RequestFailedException {

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug( "getAccountsCount(): query={}", query );
        return accountService.count( descriptor.makeAuthToken(), query ).getData();
    }

    @Override
    public boolean isLoginUnique( String login, Long excludeId ) throws RequestFailedException {

        log.debug( "isLoginUnique(): login={}, excludeId={}", login, excludeId );

        CoreResponse< Boolean > response = accountService.checkUniqueLogin( login, excludeId );

        log.debug( "isLoginUnique() -> {}, {}", response.getStatus(), response.getData() != null ? response.getData() : null );

        if ( response.isError() )
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public boolean removeAccount( Long accountId ) throws RequestFailedException {
        log.debug( "removeAccount(): id={}", accountId );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< Boolean > response = accountService.removeAccount( descriptor.makeAuthToken(), accountId );
        log.debug( "removeAccount(): result={}", response.isOk() ? "ok" : response.getStatus() );

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
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
    AccountService accountService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}
