package ru.protei.portal.core.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.core.utils.SessionIdGen;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by michael on 22.06.16.
 */
public class AuthInterceptor implements HandlerInterceptor {

    public static final String AUTH_HANDLER_LOG_PREFIX = "** AUTH-HANDLER **";

    private static Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private SessionIdGen sidGen;


    public AuthInterceptor() {

    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.debug(AUTH_HANDLER_LOG_PREFIX + ", on request to : " + request.getRequestURI());

        request.setAttribute(SecurityDefs.CLIENT_IP_REQ_ATTR, request.getRemoteAddr());
        request.setAttribute(SecurityDefs.CLIENT_UA_REQ_ATTR, request.getHeader(SecurityDefs.USER_AGENT_HEADER));

        UserSessionDescriptor descriptor = processSessionDesc(request, response);

        // check if login-action
        if (((HandlerMethod)handler).getBean() instanceof  LoginController) {
            log.debug(AUTH_HANDLER_LOG_PREFIX + ", pass to login controller");
            return true;
        }

        if (descriptor == null) {
            /**
             * @TODO replace this to configurable implementation.
             * In case of direct call for our api there is no reason to redirect, just send http-status
             *
             */
            response.sendRedirect(SecurityDefs.LOGIN_PAGE_URI);
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


    protected UserSessionDescriptor processSessionDesc(HttpServletRequest request, HttpServletResponse response) {

        String appSessionId = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie c : cookies) {
                if (c.getName().equalsIgnoreCase(SecurityDefs.APP_SESSION_ID_NAME)) {
                    appSessionId = c.getValue();
                    log.debug("found app-session id:" + appSessionId);
                    request.setAttribute(SecurityDefs.APP_SESSION_ID_NAME, appSessionId);
                    break;
                }
            }
        }

        if (appSessionId == null) {
            log.debug("no app-session found, generate id");
            appSessionId = sidGen.generateId();

            Cookie cookie = new Cookie(SecurityDefs.APP_SESSION_ID_NAME, appSessionId);
            cookie.setPath("/");
            cookie.setMaxAge(AuthService.DEF_APP_SESSION_LIVE_TIME);
            response.addCookie(cookie);
            request.setAttribute(SecurityDefs.APP_SESSION_ID_NAME, appSessionId);
            return null;
        }


        UserSessionDescriptor descriptor = authService.findSession(appSessionId, request.getRemoteAddr(),
                request.getHeader(SecurityDefs.USER_AGENT_HEADER));

        if (descriptor != null) {
            request.setAttribute(SecurityDefs.AUTH_SESSION_DESC, descriptor);
        }

        return descriptor;
    }


}
