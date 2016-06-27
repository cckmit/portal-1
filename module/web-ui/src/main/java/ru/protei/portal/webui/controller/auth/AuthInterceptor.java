package ru.protei.portal.webui.controller.auth;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.UserSession;
import ru.protei.portal.core.utils.SessionIdGen;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
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

    @Autowired
    private SessionIdGen sidGen;

    private Map<String, UserSessionDescriptor> sessionCache;


    public AuthInterceptor() {
        sessionCache = new HashMap<>();
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.debug(AUTH_HANDLER_LOG_PREFIX + ", on request to : " + request.getRequestURI());

        // check if login-action
        if (((HandlerMethod)handler).getBean() instanceof  LoginController) {
            log.debug(AUTH_HANDLER_LOG_PREFIX + ", pass to login controller");
            // create new user-session to provide for login-controller
            UserSession s = new UserSession();
            s.setClientIp(request.getRemoteAddr());
            s.setCreated(new Date());
            s.setSessionId(sidGen.generateId());

            UserSessionDescriptor descriptor = new UserSessionDescriptor();
            descriptor.init(s);

            request.setAttribute(SecurityDefs.AUTH_SESSION_DESC, descriptor);

            return true;
        }

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
        // check if login-action
        if (((HandlerMethod)handler).getBean() instanceof  LoginController) {
            log.debug(AUTH_HANDLER_LOG_PREFIX + ", post-handle of login controller");

            UserSessionDescriptor descriptor = (UserSessionDescriptor)request.getAttribute(SecurityDefs.AUTH_SESSION_DESC);
            if (descriptor.isValid()) {
                log.debug("login success, store session in cache");
                sessionCache.put(descriptor.getSessionId(), descriptor);

                Cookie cookie = new Cookie(SecurityDefs.APP_SESSION_ID_NAME, descriptor.getSessionId());
                cookie.setPath("/");
                cookie.setMaxAge(descriptor.getTimeToLive());
                response.addCookie(cookie);
            }
        }
        else if (((HandlerMethod)handler).getBean() instanceof  LogoutController) {
            UserSessionDescriptor descriptor = (UserSessionDescriptor)request.getAttribute(SecurityDefs.AUTH_SESSION_DESC);
            if (descriptor != null) {
                sessionCache.remove(descriptor.getSessionId());
                descriptor.close();
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }


    protected UserSessionDescriptor getUserSessionDesc(HttpServletRequest request) {

        // get from cache

        String appSessionId = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equalsIgnoreCase(SecurityDefs.APP_SESSION_ID_NAME)) {
                appSessionId = cookie.getValue();
                log.debug("found app-session id:" + appSessionId);
                break;
            }
        }

        if (appSessionId == null) {
            log.debug("no app-session found");
            return null;
        }


        UserSessionDescriptor descriptor = sessionCache.get(appSessionId);

        if (descriptor == null) {
            log.debug(AUTH_HANDLER_LOG_PREFIX + " no session found in cache, id=" + appSessionId);

            // try to restore from database
            UserSession appSession = sessionDAO.findBySID(appSessionId);

            if (appSession != null) {
                //ok
                descriptor = new UserSessionDescriptor();
                descriptor.init(appSession);
                descriptor.login(userloginDAO.get(appSession.getLoginId()),
                        userRoleDAO.get((long) appSession.getRoleId()),
                        personDAO.get(appSession.getPersonId()),
                        companyDAO.get(appSession.getCompanyId())
                );

                sessionCache.put(appSession.getSessionId(), descriptor);
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
