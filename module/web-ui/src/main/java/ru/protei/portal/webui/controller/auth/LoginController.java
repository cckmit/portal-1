package ru.protei.portal.webui.controller.auth;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private static Logger logger = Logger.getLogger(LoginController.class);


    @RequestMapping(value = "/do.login", method = RequestMethod.POST)
    public String login (@RequestParam(name = "ulogin",required = false)String ulogin,
                         @RequestParam(name = "upass",required = false)String upass,
                         @RequestAttribute(name=SecurityDefs.CLIENT_IP_REQ_ATTR,required = true) String clientIp
                        ) {

        logger.debug("invoke login process: " + ulogin + " from " + clientIp);

        // make checks

        return "redirect:ws-index";
        //return null;
    }

}