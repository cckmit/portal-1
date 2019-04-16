package ru.protei.portal.mock;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.user.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AuthServiceMock implements AuthService {
    UserSessionDescriptor descriptor = new UserSessionDescriptor();


    {
        Person person = new Person(0L);
        person.setFirstName("TFN");
        person.setLastName("TLN");
        person.setDisplayName("TEST user");
        Company company = new Company(0L);
        person.setCompany(company);
        UserLogin userLogin = makeLogin( person );
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

    private UserLogin makeLogin( Person person ) {
        UserLogin userLogin = new UserLogin();
        userLogin.setPerson( person );

        userLogin.setRoles( makeRoles() );
        return userLogin;
    }

    private HashSet<UserRole> makeRoles() {
        UserRole role = new UserRole();
        role.setPrivileges( new HashSet<>( Arrays.asList( En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT ) ) );
        return new HashSet<UserRole>( Arrays.asList( role ) );
    }


}
