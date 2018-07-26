package ru.protei.portal.mock;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.user.AuthService;

import javax.servlet.http.HttpServletRequest;

public class TestAuthService implements AuthService {
    UserSessionDescriptor descriptor = new UserSessionDescriptor();

    {
        Person person = new Person(0L);
        person.setFirstName("TFN");
        person.setLastName("TLN");
        person.setDisplayName("TEST user");
        Company company = new Company(0L);
        person.setCompany(company);
        UserLogin userLogin = new UserLogin();
        userLogin.setPerson(person);
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
}
