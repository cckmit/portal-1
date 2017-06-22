package ru.protei.portal.ui.account.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.ui.common.client.service.AccountService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Реализация сервиса по работе с учетными записями
 */
@Service( "AccountService" )
public class AccountServiceImpl implements AccountService {

    @Override
    public List< UserLogin > getAccounts( AccountQuery query ) throws RequestFailedException {

        log.debug( "getAccounts(): query={}", query);

        CoreResponse< List< UserLogin > > response = accountService.accountList( query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public UserLogin getAccount( long id ) throws RequestFailedException {
        log.debug( "getAccount(): id={}", id );

        CoreResponse< UserLogin > response = accountService.getAccount( id );

        log.debug( "getAccount(): id={} -> {} ", id, response.isError() ? "error" : response.getData().getUlogin() );

        return response.getData();
    }

    @Override
    public UserLogin saveAccount( UserLogin userLogin ) throws RequestFailedException {
        log.debug( "saveAccount(): account={} ", userLogin );

        if ( userLogin == null ) {
            throw new RequestFailedException( En_ResultStatus.INTERNAL_ERROR );
        }

        if ( !isLoginUnique( userLogin.getUlogin(), userLogin.getId() ) )
            throw new RequestFailedException ( En_ResultStatus.ALREADY_EXIST );

        CoreResponse< UserLogin > response = accountService.saveAccount( userLogin );

        log.debug( "saveAccount(): result={}", response.isOk() ? "ok" : response.getStatus() );

        if ( response.isOk() ) {
            log.debug( "saveAccount(): applied id={}", response.getData().getId() );
            return response.getData();
        }

        throw new RequestFailedException( response.getStatus() );
    }

    @Override
    public Long getAccountsCount( AccountQuery query ) throws RequestFailedException {
        log.debug( "getAccountsCount(): query={}", query );
        return accountService.count( query ).getData();
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

        CoreResponse< Boolean > response = accountService.removeAccount( accountId );
        log.debug( "removeAccount(): result={}", response.isOk() ? "ok" : response.getStatus() );

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Autowired
    ru.protei.portal.core.service.AccountService accountService;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}
