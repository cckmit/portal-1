package ru.protei.portal.api;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserSession;
import ru.protei.portal.core.utils.SessionIdGen;

import java.util.Date;

/**
 * Created by michael on 25.05.16.
 */
public class AuthControllerImpl implements AuthController {

    @Autowired
    private UserLoginDAO uloginDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CompanyDAO companyDAO;

    @Autowired
    private UserRoleDAO uroleDAO;

    @Autowired
    private UserSessionDAO sessionDAO;

    @Autowired
    private SessionIdGen sidGen;


    public HttpResponse login(String uname, String upass, String sessionId, String clientIp) {
        UserLogin login = uloginDAO.findByLogin(uname);
        HttpResponse resp = null;

        if (login == null) {
            return ru.protei.winter.http.HttpUtils.makeResponse(HttpResponseStatus.UNAUTHORIZED);
        }

        if (login.isLDAP_Auth()) {
            //
        }
        else {
           // check MD5
        }


        Person person = personDAO.get(login.getPersonId());


        UserSession exSession = sessionId == null ? null : sessionDAO.findBySID(sessionId);

        if (exSession != null) {
            // check attributes
//            if (!exSession.getClientIp().equals(clientIp)) {
                sessionDAO.remove(exSession);
//            }
        }

        UserSession newSession = new UserSession();
        newSession.setCreated(new Date());
        newSession.setClientIp(clientIp);
        newSession.setCompanyId(person.getCompanyId());
        newSession.setLoginId(login.getId());
        newSession.setPersonId(login.getPersonId());
        newSession.setRoleId(login.getRoleId());
        newSession.setSessionId(sidGen.generateId());

        sessionDAO.persist(newSession);

        resp = ru.protei.winter.http.HttpUtils.makeResponse(HttpResponseStatus.OK);
//        resp.addHeader("");
//        resp.addHeader();
        return resp;
    }



    public HttpResponse webCheckSession(
             String webSessionId,
             String clientIp
    ) {
        HttpResponse resp = ru.protei.winter.http.HttpUtils.makeResponse(HttpResponseStatus.FORBIDDEN);
        System.out.println("Incoming check-request, from " + clientIp == null ? "unknown" : clientIp);
        System.out.println("Check for session " + webSessionId == null ? "-" : webSessionId);
        return resp;
    }
}
