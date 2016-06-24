package ru.protei.portal.webui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.portal.webui.controller.auth.SecurityDefs;
import ru.protei.portal.webui.controller.auth.UserSessionDescriptor;

/**
 * Created by michael on 24.06.16.
 */
@Controller
public class IndexViewController {

    @GetMapping("/ws/")
    public ModelAndView workspaceIndexPage (@RequestAttribute(name = SecurityDefs.AUTH_SESSION_DESC)UserSessionDescriptor sd) {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("ws-index");
        mv.addObject("sd", sd);
        return mv;
    }

}
