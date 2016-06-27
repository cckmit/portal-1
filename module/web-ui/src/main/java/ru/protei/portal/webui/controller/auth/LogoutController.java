package ru.protei.portal.webui.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.protei.portal.core.model.dao.UserSessionDAO;

/**
 * Created by michael on 27.06.16.
 */
@Controller
public class LogoutController {

    @Autowired
    UserSessionDAO sessionDAO;

    @RequestMapping(value = "/do.logout")
    public String logout (@RequestAttribute(name=SecurityDefs.AUTH_SESSION_DESC,required = false) UserSessionDescriptor descriptor) {

        if (descriptor != null) {
            sessionDAO.remove(descriptor.getSession());
        }

        return "redirect:/login.html";
    }
}
