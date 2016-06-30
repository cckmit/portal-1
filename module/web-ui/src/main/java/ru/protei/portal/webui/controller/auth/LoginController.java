package ru.protei.portal.webui.controller.auth;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.core.service.user.AuthResult;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.webui.api.CoreResponse;

@RestController
public class LoginController {

    private static Logger logger = Logger.getLogger(LoginController.class);

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/api/do.login")
    public CoreResponse<String> login(@RequestParam(name = "ulogin") String ulogin,
                                      @RequestParam(name = "upass") String upass,
                                      @RequestAttribute(name = SecurityDefs.APP_SESSION_ID_NAME) String appSessionId,
                                      @RequestAttribute(name = SecurityDefs.CLIENT_IP_REQ_ATTR) String userIp,
                                      @RequestAttribute(name = SecurityDefs.CLIENT_UA_REQ_ATTR) String userAgent
    ) {

        logger.debug("invoke login process: " + ulogin + "@" + userIp + "/" + userAgent);

        AuthResult result = authService.login(appSessionId, ulogin, upass, userIp, userAgent);

        if (result.isOk())
            return new CoreResponse<String>().success("ok").redirect(SecurityDefs.MAIN_WORKSPACE_URI);

        return new CoreResponse<String>("Invalid login or password");
    }


    @RequestMapping(value = "/api/do.logout")
    public CoreResponse<String> logout(@RequestAttribute(name = SecurityDefs.APP_SESSION_ID_NAME) String appSessionId,
                                       @RequestAttribute(name = SecurityDefs.CLIENT_IP_REQ_ATTR) String userIp,
                                       @RequestAttribute(name = SecurityDefs.CLIENT_UA_REQ_ATTR) String userAgent) {

        if (authService.logout(appSessionId, userIp, userAgent)) {
            logger.debug("logout success, sid=" + appSessionId);
        }

        return new CoreResponse<String>().redirect(SecurityDefs.LOGIN_PAGE_URI);
    }

}