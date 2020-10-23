package ru.protei.portal.ui.account.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.portal.core.service.AccountService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.AccountController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

/**
 * Реализация сервиса по работе с учетными записями
 */
@Service( "AccountController" )
public class AccountControllerImpl implements AccountController {

    @Override
    public SearchResult<UserLogin> getAccounts(AccountQuery query) throws RequestFailedException {
        log.info("getAccounts(): query={}", query);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<SearchResult<UserLogin>> result = accountService.getAccounts(token, query);
        return checkResultAndGetData(result);
    }

    @Override
    public List<UserLoginShortView> getUserLoginShortViewList(UserLoginShortViewQuery query) throws RequestFailedException {
        log.info("getUserLoginShortViewList(): query={}", query);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(accountService.getUserLoginShortViewList(token, query));
    }

    @Override
    public UserLogin getAccount( long id ) throws RequestFailedException {
        log.info( "getAccount(): id={}", id );

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result< UserLogin > response = accountService.getAccount( token, id );

        log.info( "getAccount(): id={} -> {} ", id, response.isError() ? "error" : response.getData().getUlogin() );

        return response.getData();
    }

    @Override
    public List<UserLogin> getContactAccount(long personId ) throws RequestFailedException {
        log.info( "getContactAccount(): personId={}", personId );

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result<List<UserLogin>> response = accountService.getContactAccount( token, personId );

        log.info( "getContactAccount(): personId={} -> {} ", personId, response.isError() ? "error" : response.getData() );

        return response.getData();
    }

    @Override
    public UserLogin saveAccount( UserLogin userLogin, Boolean sendWelcomeEmail ) throws RequestFailedException {
        log.info( "saveAccount(): account={} ", userLogin );

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        if ( userLogin == null ) {
            throw new RequestFailedException( En_ResultStatus.INTERNAL_ERROR );
        }

        if ( !isLoginUnique( userLogin.getUlogin(), userLogin.getId() ) )
            throw new RequestFailedException ( En_ResultStatus.ALREADY_EXIST );

        Result< UserLogin > response = accountService.saveAccount( token, userLogin, sendWelcomeEmail );

        log.info( "saveAccount(): result={}", response.isOk() ? "ok" : response.getStatus() );

        if ( response.isOk() ) {
            log.info( "saveAccount(): applied id={}", response.getData().getId() );
            return response.getData();
        }

        throw new RequestFailedException( response.getStatus() );
    }

    @Override
    public boolean isLoginUnique( String login, Long excludeId ) throws RequestFailedException {

        log.info( "isLoginUnique(): login={}, excludeId={}", login, excludeId );

        Result< Boolean > response = accountService.checkUniqueLogin( login, excludeId );

        log.info( "isLoginUnique() -> {}, {}", response.getStatus(), response.getData());

        return checkResultAndGetData(response);
    }

    @Override
    public Long removeAccount( Long accountId ) throws RequestFailedException {
        log.info( "removeAccount(): id={}", accountId );

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result< Long > response = accountService.removeAccount( token, accountId );
        log.info( "removeAccount(): result={}", response.isOk() ? "ok" : response.getStatus() );

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public void updateAccountPassword(Long loginId, String currentPassword, String newPassword) throws RequestFailedException {
        log.info("updateAccountPassword(): id={}, newPassword={}", loginId, newPassword);

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result<?> response = accountService.updateAccountPassword(token, loginId, currentPassword, newPassword);

        log.info("updateAccountPassword(): result={}", response.isOk() ? "ok" : response.getStatus());

        if (!response.isOk()) {
            throw new RequestFailedException(response.getStatus());
        }
    }

    @Override
    public String getLoginByPersonId(Long personId) throws RequestFailedException {
        log.info("getLoginByPersonId(): personId={}", personId);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(accountService.getLoginByPersonId(token, personId));
    }

    @Autowired
    AccountService accountService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(AccountControllerImpl.class);
}
