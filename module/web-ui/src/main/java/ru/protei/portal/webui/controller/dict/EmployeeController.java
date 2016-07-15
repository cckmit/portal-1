package ru.protei.portal.webui.controller.dict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 06.07.16.
 */
@Controller
@RequestMapping("/ws/resource/")
public class EmployeeController {

    @Autowired
    PersonDAO personDAO;

    @Autowired
    PersonAbsenceDAO personAbsenceDAO;

    @RequestMapping("employees.js")
    public ModelAndView getEmployeesBase () {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("employees-script");
        mv.addObject("employees", personDAO.getEmployeesAll());

        List<PersonAbsence> currentAbsences = personAbsenceDAO.getCurrentAbsences(null);
        List<Long> missingEmployeesIDs = new ArrayList<>(currentAbsences.size());
        for (PersonAbsence absence: currentAbsences)
            missingEmployeesIDs.add(absence.getPersonId());
        mv.addObject("missingEmployeesIDs", missingEmployeesIDs);

        return mv;
    }

}
