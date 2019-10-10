package ru.protei.portal.mock;

import org.apache.commons.lang3.time.DateUtils;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.test.service.BaseServiceTest;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.api.struct.Result.error;

public class AuthServiceMock implements AuthService {

    private static final ThreadLocal<UserSessionDescriptor> descriptorThreadScoped = new ThreadLocal<UserSessionDescriptor>();

    public static final AuthToken TEST_AUTH_TOKEN = null;

    private static final En_Privilege[] PRIVILEGES = new En_Privilege[] {
            En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_CREATE,
            En_Privilege.PRODUCT_VIEW, En_Privilege.PRODUCT_EDIT, En_Privilege.PRODUCT_CREATE,
            En_Privilege.COMPANY_VIEW, En_Privilege.COMPANY_EDIT, En_Privilege.COMPANY_CREATE,
            En_Privilege.CONTACT_VIEW, En_Privilege.CONTACT_EDIT, En_Privilege.CONTACT_CREATE,
    };

    private UserSessionDescriptor stubDescriptor;

    public AuthServiceMock() {
        Company company = new Company( 0L );
        Person person =  BaseServiceTest.createNewPerson(  new Company( 0L ) );
        person.setId( 0L );
        UserLogin userLogin = makeLogin( person );
        stubDescriptor = makeDescriptor( userLogin, person, company );
    }

    private UserSessionDescriptor makeDescriptor( UserLogin userLogin, Person person, Company company ) {
        UserSessionDescriptor descriptor = new UserSessionDescriptor();
        descriptor.init( makeUserSession( userLogin, person ) );
        descriptor.login( userLogin, person, company );
        return descriptor;
    }

    @Override
    public UserSessionDescriptor findSession(String appSessionId, String ip, String userAgent) {
        return getDescriptor();
    }

    @Override
    public UserSessionDescriptor findSession(AuthToken token) {
        return findSession( null, null, null);
    }

    @Override
    public Result<UserSessionDescriptor> login( String appSessionID, String login, String pwd, String ip, String userAgent) {
        return ok( getDescriptor() );
    }

    public void makeThreadDescriptor( UserLogin userLogin, Person person, Company company ) {
        setThreadDescriptor(makeDescriptor(userLogin, person, company));
    }

    @Override
    public boolean logout(String appSessionId, String ip, String userAgent) {
        return true;
    }

    @Override
    public UserSessionDescriptor getUserSessionDescriptor(HttpServletRequest request) {
        return getDescriptor();
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

    private UserSessionDescriptor getDescriptor() {
        UserSessionDescriptor descriptor = getThreadDescriptor();
        if (descriptor == null) return stubDescriptor;
        return descriptor;
    }

    private final static UserSessionDescriptor getThreadDescriptor() {
        return descriptorThreadScoped.get();
    }

    private final static void setThreadDescriptor( UserSessionDescriptor client ) {
        descriptorThreadScoped.set( client );
    }
}
