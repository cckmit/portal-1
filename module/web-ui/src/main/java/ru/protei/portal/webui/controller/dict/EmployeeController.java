package ru.protei.portal.webui.controller.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.portal.core.model.dao.PersonDAO;

/**
 * Created by michael on 06.07.16.
 */
@Controller
@RequestMapping("/ws/resource/")
public class EmployeeController {

    @Autowired
    PersonDAO personDAO;

    @RequestMapping("employees.js")
    public ModelAndView getEmployeesBase () {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("employees-script");
        mv.addObject("employees", personDAO.getEmployeesAll());

        return mv;
    }

}
