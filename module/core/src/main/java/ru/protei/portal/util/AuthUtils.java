package ru.protei.portal.util;

import org.slf4j.Logger;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.controller.api.Credentials;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.utils.SessionIdGen;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.core.model.util.CrmConstants.Header.X_REAL_IP;

public class AuthUtils {
    public static Result<AuthToken> authenticate(HttpServletRequest request, HttpServletResponse response, AuthService authService, SessionIdGen sidGen, Logger logger) {
        Credentials cr = null;
        try {
            cr = Credentials.parse( request.getHeader( "Authorization" ) );
            if ((cr == null) || (!cr.isValid())) {
                String logMsg = "Basic authentication required";
                response.setHeader( "WWW-Authenticate", "Basic realm=\"" + logMsg + "\"" );
                response.sendError( HttpServletResponse.SC_UNAUTHORIZED );
                logger.error( "API | {}", logMsg );
                return error( En_ResultStatus.INVALID_LOGIN_OR_PWD );
            }

        } catch (IllegalArgumentException | IOException ex) {
            logger.error( "Can`t authenticate {}", ex.getMessage() );
            return error( En_ResultStatus.AUTH_FAILURE );
        } catch (Exception ex) {
            logger.error( "Can`t authenticate {} unexpected exception: ", ex );
            return error( En_ResultStatus.AUTH_FAILURE );
        }

        String ip = request.getHeader(X_REAL_IP);
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        String userAgent = request.getHeader(CrmConstants.Header.USER_AGENT);

        logger.debug( "API | Authentication: ip={}, user={}", ip, cr.login );
        return authService.login( sidGen.generateId(), cr.login, cr.password, ip, userAgent )
                .ifError( result -> {
                    result.setMessage( "Authentification error" );
                    logger.error( "API | error {}", result );
                } );
    }
}
