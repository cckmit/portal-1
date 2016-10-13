package ru.protei.portal.ui.crm.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthResult;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.crm.client.service.AuthService;
import ru.protei.portal.ui.crm.server.util.SystemConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * Сервис авторизации
 */
@Service( "AuthService" )
public class AuthServiceImpl extends RemoteServiceServlet implements AuthService {

    @Override
    public Profile authentificate( String login, String password ) throws RequestFailedException {
        if ( login == null && password == null ) {
            log.debug( "authentificate: empty auth params" );

            UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpRequest );
            log.debug( "authentificate: sessionDescriptior={}", descriptor );

            return descriptor == null ? null : makeProfileByDescriptor( descriptor );
        }

        log.debug( "authentificate: login={}", login );

        AuthResult result = authService.login( httpRequest.getSession().getId(), login, password, httpRequest.getRemoteAddr(), httpRequest.getHeader( SystemConstants.USER_AGENT_HEADER ) );
        if ( !result.isOk() ) {
            throw new RequestFailedException( result.getResult().name() );
        }

        //sessionService.setUserSessionDescriptor( httpRequest, result.getDescriptor() );
        return makeProfileByDescriptor( result.getDescriptor() );
    }

    @Override
    public void logout() {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpRequest );
        if ( descriptor != null )  {
            authService.logout( httpRequest.getSession().getId(), httpRequest.getRemoteAddr(), httpRequest.getHeader( SystemConstants.USER_AGENT_HEADER ) );
        }
    }

    private Profile makeProfileByDescriptor( UserSessionDescriptor sessionDescriptor ) {
        Profile profile = new Profile();
        profile.setRole( sessionDescriptor.getUrole() );
        profile.setLogin( sessionDescriptor.getLogin().getUlogin() );
        profile.setName( sessionDescriptor.getPerson().getFirstName() );

        return profile;
    }

    @Autowired
    HttpServletRequest httpRequest;

    @Autowired
    SessionService sessionService;

    @Autowired
    private ru.protei.portal.core.service.user.AuthService authService;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}