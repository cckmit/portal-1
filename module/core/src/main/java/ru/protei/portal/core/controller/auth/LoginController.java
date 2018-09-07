package ru.protei.portal.core.controller.auth;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.service.user.AuthService;

@RestController
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

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

        CoreResponse<UserSessionDescriptor> result = authService.login(appSessionId, ulogin, upass, userIp, userAgent);

        if (result.isError())
            return new CoreResponse().error(En_ResultStatus.INVALID_LOGIN_OR_PWD);


        return new CoreResponse<String>().success("ok").redirect(SecurityDefs.MAIN_WORKSPACE_URI);
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