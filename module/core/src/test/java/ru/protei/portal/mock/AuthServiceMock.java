package ru.protei.portal.mock;

import org.apache.commons.lang3.time.DateUtils;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.test.service.BaseServiceTest;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static ru.protei.portal.api.struct.Result.ok;

public class AuthServiceMock implements AuthService {

    private static final ThreadLocal<AuthToken> authTokenThreadScoped = new ThreadLocal<>();

    public static final AuthToken TEST_AUTH_TOKEN = null;

    private static final En_Privilege[] PRIVILEGES = new En_Privilege[] {
            En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_CREATE,
            En_Privilege.PRODUCT_VIEW, En_Privilege.PRODUCT_EDIT, En_Privilege.PRODUCT_CREATE,
            En_Privilege.COMPANY_VIEW, En_Privilege.COMPANY_EDIT, En_Privilege.COMPANY_CREATE,
            En_Privilege.CONTACT_VIEW, En_Privilege.CONTACT_EDIT, En_Privilege.CONTACT_CREATE,
    };

    private AuthToken stubAuthToken;

    public AuthServiceMock() {
        Company company = new Company( 0L );
        Person person =  BaseServiceTest.createNewPerson( company );
        person.setId( 0L );
        UserLogin userLogin = makeLogin( person );
        UserSession userSession = makeUserSession(userLogin, person);
        stubAuthToken = makeAuthToken( userSession );
    }

    @Override
    public Result<AuthToken> login( String appSessionID, String login, String pwd, String ip, String userAgent ) {
        return ok( getAuthToken() );
    }

    public void makeThreadDescriptor( UserLogin userLogin, Person person ) {
        UserSession userSession = makeUserSession(userLogin, person);
        setThreadAuthToken(makeAuthToken(userSession));
    }

    public void resetThreadDescriptor(  ) {
        setThreadAuthToken(null);
    }

    @Override
    public boolean logout(String appSessionId, String ip, String userAgent) {
        return true;
    }

    @Override
    public Result<AuthToken> validateAuthToken(AuthToken token) {
        return ok(token);
    }

    private UserSession makeUserSession(UserLogin login, Person person) {
        UserSession session = new UserSession();
        session.setSessionId("test-session-id");
        session.setClientIp("127.0.0.1");
        session.setLoginId(login.getId());
        session.setPersonId(login.getPersonId());
        session.setCompanyId(person.getCompanyId());
        session.setCreated(new Date());
        session.setExpired(DateUtils.addHours(new Date(), 3));
        return session;
    }

    private UserLogin makeLogin(Person person) {
        UserLogin userLogin = new UserLogin();
        userLogin.setId(1L);
        userLogin.setPerson(person);
        userLogin.setRoles(makeRoles());
        return userLogin;
    }

    private HashSet<UserRole> makeRoles() {
        UserRole role = new UserRole();
        role.setPrivileges(new HashSet<>(Arrays.asList(PRIVILEGES)));
        role.setScope(En_Scope.SYSTEM);
        return new HashSet<>(Arrays.asList(role));
    }

    private AuthToken makeAuthToken(UserSession userSession) {
        if (userSession == null) {
            return null;
        }
        return new AuthToken(
            userSession.getSessionId(),
            userSession.getClientIp(),
            userSession.getLoginId(),
            userSession.getPersonId(),
            userSession.getCompanyId(),
            makeRoles()
        );
    }

    public AuthToken getAuthToken() {
        AuthToken authToken = getThreadAuthToken();
        if (authToken == null) return stubAuthToken;
        return authToken;
    }

    private static AuthToken getThreadAuthToken() {
        return authTokenThreadScoped.get();
    }

    private static void setThreadAuthToken( AuthToken client ) {
        authTokenThreadScoped.set( client );
    }
}
