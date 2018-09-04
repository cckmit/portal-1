package ru.protei.portal.ui.crm.server.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_PrivilegeEntity;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.yt.WorkItem;
import ru.protei.portal.core.service.YtRepository;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.crm.client.service.AuthController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Сервис авторизации
 */
@Service( "AuthController" )
public class AuthControllerImpl implements AuthController {

    @Override
    public Profile authentificate( String login, String password ) throws RequestFailedException {

        __debug();

        if ( login == null && password == null ) {
            log.debug( "authentificate: empty auth params" );

            UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpRequest );
            log.debug( "authentificate: sessionDescriptior={}", descriptor );

            return descriptor == null ? null : makeProfileByDescriptor( descriptor );
        }

        log.debug( "authentificate: login={}", login );

        CoreResponse< UserSessionDescriptor > result = authService.login( httpRequest.getSession().getId(), login, password, httpRequest.getRemoteAddr(), httpRequest.getHeader( CrmConstants.Header.USER_AGENT ) );
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
            authService.logout( httpRequest.getSession().getId(), httpRequest.getRemoteAddr(), httpRequest.getHeader( CrmConstants.Header.USER_AGENT ) );
            sessionService.setUserSessionDescriptor( httpRequest, null );
        }
    }

    private void __debug() {
        ytRepository.getTasks( null, "2018-08-21", YtRepository.DateType.WORK_ITEMS ).forEach((issue)->{
            WorkItem[] changes = null;
            try {
                changes = ytRepository.getIssueWorkItems( issue.getId() );
            }
            catch ( HttpClientErrorException e ) {
                if ( e.getStatusCode().equals( HttpStatus.BAD_REQUEST ) ) {
                    return;
                }
            }

            if (changes == null) {
                System.out.println( "No changes: "+issue.getId() );
                return;
            }

            System.out.println( changes );
        });
    }

    private Profile makeProfileByDescriptor( UserSessionDescriptor sessionDescriptor ) {
        Profile profile = new Profile();
        Set< UserRole > roles = sessionDescriptor.getLogin().getRoles();
        profile.setRoles( roles );
        profile.setLogin( sessionDescriptor.getLogin().getUlogin() );
        profile.setName( sessionDescriptor.getPerson().getFirstName() );
        profile.setShortName( sessionDescriptor.getPerson().getDisplayShortName() );
        profile.setFullName( sessionDescriptor.getPerson().getDisplayName() );
        profile.setIsFired( sessionDescriptor.getPerson().isFired());
        profile.setId( sessionDescriptor.getPerson().getId() );
        profile.setPrivileges( collectPrivileges( roles ) );
        profile.setGender( sessionDescriptor.getPerson().getGender() );
        profile.setCompany( sessionDescriptor.getCompany() );

        return profile;
    }

    private Set< En_Privilege > collectPrivileges( Set< UserRole > roles ) {
        if ( roles == null ) {
            return Collections.emptySet();
        }

        Set<En_Privilege> userPrivileges = new HashSet<>();
        Map<En_PrivilegeEntity, Set<En_Scope>> privilegeEntityToScope = new HashMap<>();
        for ( UserRole role : roles ) {
            if ( role.getPrivileges() == null || role.getScope() == null ) {
                continue;
            }

            userPrivileges.addAll( role.getPrivileges() );
            for ( En_Privilege privilege : role.getPrivileges() ) {
                Set<En_Scope> scopes = privilegeEntityToScope.computeIfAbsent(privilege.getEntity(), k -> new HashSet<>());
                scopes.add( role.getScope() );
            }
        }

        // дополняем список клиентских привилегий по scope
        Set<En_Scope> scopesIssueEntity = privilegeEntityToScope.get( En_PrivilegeEntity.ISSUE );
        if ( !CollectionUtils.isEmpty( scopesIssueEntity ) && scopesIssueEntity.contains( En_Scope.SYSTEM ) ) {
            userPrivileges.addAll( Arrays.asList( En_Privilege.CLIENT_SYSTEM_SCOPE_PRIVILEGES) );
        }

        return userPrivileges;
    }

    @Autowired
    HttpServletRequest httpRequest;

    @Autowired
    SessionService sessionService;

    @Autowired
    private AuthService authService;

    @Autowired
    YtRepository ytRepository;

    private static final Logger log = LoggerFactory.getLogger(AuthControllerImpl.class);
}