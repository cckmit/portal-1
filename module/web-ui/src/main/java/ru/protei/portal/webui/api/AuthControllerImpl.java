package ru.protei.portal.webui.api;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.utils.SessionIdGen;

/**
 * Created by michael on 25.05.16.
 */
public class AuthControllerImpl {

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

/*
    public HttpResponse login(String uname, String upass, String sessionId, String clientIp) {
        UserLogin login = uloginDAO.findByLogin(uname);
        HttpResponse resp = null;

        if (login == null) {
            return ru.protei.winter.http.HttpUtils.makeResponse(HttpResponseStatus.UNAUTHORIZED);
        }



        UserSession exSession = sessionId == null ? null : sessionDAO.findBySID(sessionId);

        if (exSession != null) {
            // check attributes
//            if (!exSession.getClientIp().equals(clientIp)) {
                sessionDAO.remove(exSession);
//            }
        }


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

    */
}
