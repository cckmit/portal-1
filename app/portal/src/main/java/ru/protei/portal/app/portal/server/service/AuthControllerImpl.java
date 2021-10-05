package ru.protei.portal.app.portal.server.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.app.portal.client.service.AuthController;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_PrivilegeEntity;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.CompanyService;
import ru.protei.portal.core.service.PersonService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.Profile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static ru.protei.portal.core.model.util.CrmConstants.Header.X_REAL_IP;

/**
 * Сервис авторизации
 */
@Service( "AuthController" )
public class AuthControllerImpl implements AuthController {

    @Override
    public Profile authenticate( String login, String password) throws RequestFailedException {

        AuthToken token = sessionService.getAuthToken(httpRequest);

        if (token != null) {
            log.info("authentificate: token={}", token);
            return makeProfileByAuthToken(token);
        }

        if (login == null && password == null) {
            log.info( "authentificate: neither login nor password provided" );
            return null;
        }

        log.info( "authentificate: login={}", login );

        String ip = httpRequest.getHeader(X_REAL_IP);

        if (ip == null) {
            ip = httpRequest.getRemoteAddr();
        }

        token = ServiceUtils.checkResultAndGetData(authService.login(
                httpRequest.getSession().getId(),
                login,
                password,
                ip,
                httpRequest.getHeader(CrmConstants.Header.USER_AGENT)
        ));

        sessionService.setSessionLifetime(httpRequest, AuthService.DEF_APP_SESSION_LIVE_TIME);
        sessionService.setAuthToken(httpRequest, token);
        return makeProfileByAuthToken(token);
    }

    @Override
    public void logout() throws RequestFailedException {
        AuthToken token = sessionService.getAuthToken(httpRequest);
        if (token != null) {
            String ip = httpRequest.getHeader(X_REAL_IP);

            if (ip == null) {
                ip = httpRequest.getRemoteAddr();
            }

            ServiceUtils.checkResult(authService.logout(
                    token,
                    ip,
                    httpRequest.getHeader(CrmConstants.Header.USER_AGENT)
            ));
            sessionService.setAuthToken(httpRequest, null);
        }
    }

    private Profile makeProfileByAuthToken(AuthToken token) throws RequestFailedException {

        Person person = ServiceUtils.checkResultAndGetData(personService.getPerson(token, token.getPersonId()));
        Company company = ServiceUtils.checkResultAndGetData(companyService.getCompanyOmitPrivileges(token, token.getCompanyId()));
        UserLogin userLogin = ServiceUtils.checkResultAndGetData(authService.getUserLogin(token, token.getUserLoginId()));

        Profile profile = new Profile();
        profile.setLoginId(token.getUserLoginId());
        profile.setLogin(userLogin.getUlogin());
        profile.setAuthType(userLogin.getAuthType());
        profile.setRoles(token.getRoles());
        profile.setPrivileges(collectPrivileges(token.getRoles()));
        profile.setName(person.getFirstName());
        profile.setShortName(person.getDisplayShortName());
        profile.setFullName(person.getDisplayName());
        profile.setIsFired(person.isFired());
        profile.setId(person.getId());
        profile.setGender(person.getGender());
        profile.setCompany(company);
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
    AuthService authService;
    @Autowired
    PersonService personService;
    @Autowired
    CompanyService companyService;

    private static final Logger log = LoggerFactory.getLogger(AuthControllerImpl.class);
}
