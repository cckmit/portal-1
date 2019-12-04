package ru.protei.portal.mock;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.test.service.BaseServiceTest;

import java.util.Arrays;
import java.util.HashSet;

import static ru.protei.portal.api.struct.Result.ok;

public class AuthServiceMock implements AuthService {

    private static final ThreadLocal<AuthToken> authTokenThreadScoped = new ThreadLocal<>();

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
        stubAuthToken = makeAuthToken( userLogin );
    }

    @Override
    public Result<AuthToken> login( String appSessionID, String login, String pwd, String ip, String userAgent ) {
        return ok( getAuthToken() );
    }

    public void makeThreadAuthToken(UserLogin userLogin) {
        setThreadAuthToken(makeAuthToken(userLogin));
    }

    public void resetThreadAuthToken() {
        setThreadAuthToken(null);
    }

    @Override
    public Result<AuthToken> logout(AuthToken token, String ip, String userAgent) {
        return ok(token);
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

    private AuthToken makeAuthToken(UserLogin userLogin) {
        AuthToken token = new AuthToken("test-session-id");
        token.setIp("127.0.0.1");
        token.setUserLoginId(userLogin.getId());
        token.setPersonId(userLogin.getPersonId());
        token.setPersonDisplayShortName("Test user short name");
        token.setCompanyId(userLogin.getCompanyId());
        token.setCompanyAndChildIds(CollectionUtils.listOf(userLogin.getCompanyId()));
        token.setRoles(makeRoles());
        return token;
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
