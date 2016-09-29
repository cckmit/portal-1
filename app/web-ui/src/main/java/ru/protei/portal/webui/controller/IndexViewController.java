package ru.protei.portal.webui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.webui.controller.auth.SecurityDefs;

/**
 * Created by michael on 24.06.16.
 */
@Controller
public class IndexViewController {

    @RequestMapping("/index.html")
    public String globRoot (@RequestAttribute(name = SecurityDefs.AUTH_SESSION_DESC,required = false)UserSessionDescriptor sd) {
        if (sd != null && sd.isValid())
            return "redirect:/userList/";
            //return "redirect:/ws/";

        return "redirect:/login.html";
    }

//    @GetMapping("/ws/")
//    public ModelAndView workspaceIndexPage (@RequestAttribute(name = SecurityDefs.AUTH_SESSION_DESC)UserSessionDescriptor sd) {
//
//        ModelAndView mv = new ModelAndView();
//        mv.setViewName("ws-index");
//        mv.addObject("sd", sd);
//        return mv;
//    }

    @GetMapping("/userList/")
    public ModelAndView userListPage (@RequestAttribute(name = SecurityDefs.AUTH_SESSION_DESC)UserSessionDescriptor sd) {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("userList");
        mv.addObject("sd", sd);
        return mv;
    }

}
