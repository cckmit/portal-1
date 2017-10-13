package ru.protei.portal.ui.crm.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.server.util.SystemConstants;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.crm.client.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * Сервис авторизации
 */
@Service( "AuthService" )
public class AuthServiceImpl implements AuthService {

    @Override
    public Profile authentificate( String login, String password ) throws RequestFailedException {
        if ( login == null && password == null ) {
            log.debug( "authentificate: empty auth params" );

            UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpRequest );
            log.debug( "authentificate: sessionDescriptior={}", descriptor );

            return descriptor == null ? null : makeProfileByDescriptor( descriptor );
        }

        log.debug( "authentificate: login={}", login );

        CoreResponse< UserSessionDescriptor > result = authService.login( httpRequest.getSession().getId(), login, password, httpRequest.getRemoteAddr(), httpRequest.getHeader( SystemConstants.USER_AGENT_HEADER ) );
        if ( result.isError() ) {
            throw new RequestFailedException( result.getStatus() );
        }

        sessionService.setUserSessionDescriptor( httpRequest, result.getData() );
        return makeProfileByDescriptor( result.getData() );
    }

    @Override
    public void logout() {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpRequest );
        if ( descriptor != null ) {
            authService.logout( httpRequest.getSession().getId(), httpRequest.getRemoteAddr(), httpRequest.getHeader( SystemConstants.USER_AGENT_HEADER ) );
            sessionService.setUserSessionDescriptor( httpRequest, null );
        }
    }

    private Profile makeProfileByDescriptor( UserSessionDescriptor sessionDescriptor ) {
        Profile profile = new Profile();
        Set< UserRole > roles = sessionDescriptor.getLogin().getRoles();
        profile.setRoles( roles );
        profile.setLogin( sessionDescriptor.getLogin().getUlogin() );
        profile.setName( sessionDescriptor.getPerson().getFirstName() );
        profile.setId( sessionDescriptor.getPerson().getId() );
        profile.setPrivileges( getAllPrivileges( roles ) );
        profile.setCompany( sessionDescriptor.getCompany() );

        return profile;
    }

    private Set< En_Privilege > getAllPrivileges( Set< UserRole > roles ) {
        Set< En_Privilege > privileges = new HashSet<>();
        if ( roles == null ) {
            return privileges;
        }

        for ( UserRole role : roles ) {
            if ( role.getPrivileges() == null ) {
                continue;
            }

            privileges.addAll(role.getPrivileges());
        }

        return privileges;
    }

    @Autowired
    HttpServletRequest httpRequest;

    @Autowired
    SessionService sessionService;

    @Autowired
    private ru.protei.portal.core.service.user.AuthService authService;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}