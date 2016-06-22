package ru.protei.portal.webui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by michael on 20.06.16.
 */

@Controller
public class IndexView {

    @RequestMapping(path = "/index.html")
    public String processIndexPage (Model m) {

        System.out.println("Running index-page process");

        m.addAttribute("msg", "Hello world!");

        return "index";
    }
}
