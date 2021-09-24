package ru.protei.portal.mock;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.winter.core.utils.Pair;

import java.util.Arrays;
import java.util.HashSet;

import static ru.protei.portal.api.struct.Result.ok;

public class AuthServiceMock implements AuthService {

    private static final ThreadLocal<Pair<UserLogin, AuthToken>> authTokenThreadScoped = new ThreadLocal<>();

    private static final En_Privilege[] PRIVILEGES = new En_Privilege[] {
            En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_CREATE,
            En_Privilege.PRODUCT_VIEW, En_Privilege.PRODUCT_EDIT, En_Privilege.PRODUCT_CREATE,
            En_Privilege.COMPANY_VIEW, En_Privilege.COMPANY_EDIT, En_Privilege.COMPANY_CREATE,
            En_Privilege.CONTACT_VIEW, En_Privilege.CONTACT_EDIT, En_Privilege.CONTACT_CREATE,
            En_Privilege.EMPLOYEE_VIEW, En_Privilege.PROJECT_CREATE,
            En_Privilege.SUBNET_VIEW, En_Privilege.SUBNET_CREATE,
            En_Privilege.SUBNET_EDIT, En_Privilege.SUBNET_REMOVE,
            En_Privilege.RESERVED_IP_VIEW, En_Privilege.RESERVED_IP_CREATE,
            En_Privilege.RESERVED_IP_EDIT, En_Privilege.RESERVED_IP_REMOVE,
            En_Privilege.PLAN_CREATE, En_Privilege.PLAN_EDIT, En_Privilege.PLAN_REMOVE, En_Privilege.PLAN_VIEW,
            En_Privilege.PROJECT_CREATE, En_Privilege.PROJECT_EDIT, En_Privilege.PROJECT_REMOVE, En_Privilege.PROJECT_VIEW,
            En_Privilege.SITE_FOLDER_CREATE, En_Privilege.SITE_FOLDER_REMOVE, En_Privilege.SITE_FOLDER_EDIT,
            En_Privilege.ABSENCE_CREATE,
            En_Privilege.DOCUMENT_CREATE, En_Privilege.DOCUMENT_REMOVE,
            En_Privilege.DELIVERY_CREATE
    };

    private UserLogin stubUserLogin;
    private AuthToken stubAuthToken;

    public AuthServiceMock() {
        Company company = new Company( 0L );
        Person person =  BaseServiceTest.createNewPerson( company );
        person.setId( 0L );
        stubUserLogin = makeLogin( person );
        stubAuthToken = makeAuthToken( stubUserLogin );
    }

    @Override
    public Result<AuthToken> login( String appSessionID, String login, String pwd, String ip, String userAgent ) {
        return ok( getAuthToken() );
    }

    public void makeThreadAuthToken(UserLogin userLogin) {
        setThreadAuthToken(userLogin, makeAuthToken(userLogin));
    }

    public void resetThreadAuthToken() {
        setThreadAuthToken(null, null);
    }

    @Override
    public Result<AuthToken> logout(AuthToken token, String ip, String userAgent) {
        return ok(token);
    }

    @Override
    public Result<UserLogin> getUserLogin(AuthToken token, Long userLoginId) {
        UserLogin userLogin = getThreadUserLogin();
        if (userLogin == null) return ok(stubUserLogin);
        return ok(userLogin);
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
        Pair<UserLogin, AuthToken> pair = authTokenThreadScoped.get();
        return pair == null ? null : pair.getB();
    }

    private static UserLogin getThreadUserLogin() {
        Pair<UserLogin, AuthToken> pair = authTokenThreadScoped.get();
        return pair == null ? null : pair.getA();
    }

    private static void setThreadAuthToken( UserLogin userLogin, AuthToken client ) {
        if (userLogin == null && client == null) {
            authTokenThreadScoped.remove();
            return;
        }
        authTokenThreadScoped.set( new Pair<>(userLogin, client) );
    }
}
