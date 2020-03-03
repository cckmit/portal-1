package ru.protei.portal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.service.auth.AuthService;

public class AuthServiceMock implements AuthService {
    @Override
    public Result<AuthToken> login( String appSessionID, String login, String pwd, String ip, String userAgent ) {
        log.warn( "login(): Not implemented." );//TODO NotImplemented
        return Result.error( En_ResultStatus.NOT_AVAILABLE );
    }

    @Override
    public Result<AuthToken> logout( AuthToken token, String ip, String userAgent ) {
        log.warn( "logout(): Not implemented." );//TODO NotImplemented
        return Result.error( En_ResultStatus.NOT_AVAILABLE );
    }

    @Override
    public Result<UserLogin> getUserLogin( AuthToken token, Long userLoginId ) {
        log.warn( "getUserLogin(): Not implemented." );//TODO NotImplemented
        return Result.error( En_ResultStatus.NOT_AVAILABLE );
    }

    private static final Logger log = LoggerFactory.getLogger( AuthServiceMock.class );
}
