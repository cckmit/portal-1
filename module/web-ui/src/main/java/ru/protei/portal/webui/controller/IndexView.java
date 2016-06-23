package ru.protei.portal.webui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by michael on 20.06.16.
 */

@Controller
@RequestMapping("/")
public class IndexView {

    @RequestMapping("/index.htm*")
    public String processIndexPageHtml () {
        return "redirect:ws-index";
    }

    @RequestMapping("/index.jsp")
    public String processIndexPageJsp () {
        return "redirect:ws-index";
    }
}
