package ru.protei.portal.webui.controller.auth;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.UserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 22.06.16.
 */
public class AuthInterceptor implements HandlerInterceptor {

    public static final String AUTH_HANDLER_LOG_PREFIX = "** AUTH-HANDLER **";

    private static Logger log = Logger.getLogger(AuthInterceptor.class);

    @Autowired
    private UserLoginDAO userloginDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CompanyDAO companyDAO;

    @Autowired
    private UserRoleDAO userRoleDAO;

    @Autowired
    private UserSessionDAO sessionDAO;

//    @Autowired
//    private SessionIdGen sidGen;

    private Map<String, UserSessionDescriptor> sessionCache;


    public AuthInterceptor() {
        sessionCache = new HashMap<>();
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.debug(AUTH_HANDLER_LOG_PREFIX + ", on request to : " + request.getRequestURI());

        UserSessionDescriptor descriptor = getUserSessionDesc(request);
        if (descriptor == null) {

            /**
             * @TODO replace this to configurable implementation.
             * In case of direct call for our api there is no reason to redirect, just send http-status
             *
             */
            response.sendRedirect("/login.html");
            return false;
        }


        log.debug(AUTH_HANDLER_LOG_PREFIX + ", session is valid, allow request : " + request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


    protected UserSessionDescriptor getUserSessionDesc(HttpServletRequest request) {

        // get from cache
        UserSessionDescriptor descriptor = sessionCache.get(request.getSession().getId());

        if (descriptor == null) {
            log.debug(AUTH_HANDLER_LOG_PREFIX + " no session found in cache, id=" + request.getSession().getId());

            // try to restore from database
            UserSession s = sessionDAO.findBySID(request.getSession().getId());

            if (s != null) {
                //ok
                descriptor = new UserSessionDescriptor();
                descriptor.init(s);
                descriptor.login(userloginDAO.get(s.getLoginId()),
                        userRoleDAO.get((long) s.getRoleId()),
                        personDAO.get(s.getPersonId()),
                        companyDAO.get(s.getCompanyId())
                );

                sessionCache.put(s.getSessionId(), descriptor);
            }
        }

        // validate session
        if (descriptor != null && descriptor.getSession() != null) {

            if (!descriptor.isValid()) {
                log.warn("invalid session " + descriptor.getSessionId());
                closeSessionDesc(descriptor);
                return null;
            }

            if (descriptor.isExpired()) {
                log.warn("session with id " + descriptor.getSessionId() + " is expired, block request");
                closeSessionDesc(descriptor);
                return null;
            }

            if (!descriptor.getSession().getClientIp().equals(request.getRemoteAddr())) {
                log.warn("Security exception, host " + request.getRemoteAddr() + " is trying to access session " + descriptor.getSessionId() + " created for " + descriptor.getSession().getClientIp());
                return null;
            }

            // now, all is ok
            request.setAttribute(SecurityDefs.AUTH_SESSION_DESC, descriptor);

            return descriptor;
        }

        return null;
    }

    private void closeSessionDesc(UserSessionDescriptor descriptor) {
        sessionDAO.remove(descriptor.getSession());
        sessionCache.remove(descriptor.getSessionId());
        descriptor.close();
    }

}
