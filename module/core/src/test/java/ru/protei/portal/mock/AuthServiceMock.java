package ru.protei.portal.mock;

import org.apache.commons.lang3.time.DateUtils;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.user.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

public class AuthServiceMock implements AuthService {

    private static final En_Privilege[] PRIVILEGES = new En_Privilege[] {
            En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_CREATE,
            En_Privilege.PRODUCT_VIEW, En_Privilege.PRODUCT_EDIT, En_Privilege.PRODUCT_CREATE,
            En_Privilege.COMPANY_VIEW, En_Privilege.COMPANY_EDIT, En_Privilege.COMPANY_CREATE,
            En_Privilege.CONTACT_VIEW, En_Privilege.CONTACT_EDIT, En_Privilege.CONTACT_CREATE,
    };
    private UserSessionDescriptor descriptor;

    public AuthServiceMock() {
        descriptor = new UserSessionDescriptor();
        Person person = new Person(0L);
        person.setFirstName("TFN");
        person.setLastName("TLN");
        person.setDisplayName("TEST user");
        Company company = new Company(0L);
        person.setCompany(company);
        UserLogin userLogin = makeLogin(person);
        descriptor.init(makeUserSession(userLogin, person));
        descriptor.login(userLogin, person, company);
    }

    @Override
    public UserSessionDescriptor findSession(String appSessionId, String ip, String userAgent) {
        return descriptor;
    }

    @Override
    public UserSessionDescriptor findSession(AuthToken token) {
        return descriptor;
    }

    @Override
    public CoreResponse<UserSessionDescriptor> login(String appSessionID, String login, String pwd, String ip, String userAgent) {
        return new CoreResponse<UserSessionDescriptor>().success(descriptor);
    }

    @Override
    public boolean logout(String appSessionId, String ip, String userAgent) {
        return true;
    }

    @Override
    public UserSessionDescriptor getUserSessionDescriptor(HttpServletRequest request) {
        return descriptor;
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
        return new HashSet<>(Arrays.asList(role));
    }
}
