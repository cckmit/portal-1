package ru.protei.portal.webui.controller.auth;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class LoginController {

    private static Logger logger = Logger.getLogger(LoginController.class);

    @RequestMapping("/login.html")
    public String login (@RequestParam(name = "ulogin")String ulogin,
                         @RequestParam(name = "upass")String upass,
                         @RequestAttribute(name=SecurityDefs.CLIENT_IP_REQ_ATTR) String clientIp
                        ) {

        logger.debug("invoke login process: " + ulogin + " from " + clientIp);

        return "redirect:ws-index";
    }

}