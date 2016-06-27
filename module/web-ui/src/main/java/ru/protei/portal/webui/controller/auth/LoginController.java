package ru.protei.portal.webui.controller.auth;


import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;

import java.util.Date;

@Controller
public class LoginController {

    private static Logger logger = Logger.getLogger(LoginController.class);

    @Autowired
    UserLoginDAO userLoginDAO;

    @Autowired
    UserSessionDAO sessionDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private UserRoleDAO userRoleDAO;

    @Autowired
    private CompanyDAO companyDAO;


    private String getLoginPageView () {
        return "redirect:/login.html";
    }

    @RequestMapping(value = "/do.login", method = RequestMethod.POST)
    public String login (@RequestParam(name = "ulogin")String ulogin,
                         @RequestParam(name = "upass")String upass,
                         @RequestAttribute(name=SecurityDefs.AUTH_SESSION_DESC,required = true) UserSessionDescriptor descriptor
                        ) {

        logger.debug("invoke login process: " + ulogin + "@" + descriptor.getSession().getClientIp());


//        userLoginDAO.getByCondition("")
        // make checks

        UserLogin login = userLoginDAO.findByLogin(ulogin);
        if (login == null) {
            logger.debug("login [" + ulogin + "] not found, auth-failed");
            return getLoginPageView();
        }

        if (login.isLDAP_Auth()) {
            // check by LDAP
        }
        else {
            // check MD5
            String md5Hash = DigestUtils.md5DigestAsHex(upass.getBytes());
            if (login.getUpass() == null || !login.getUpass().equalsIgnoreCase(md5Hash)) {
                logger.debug("login " + ulogin + " - invalid password, auth-failed");
                return getLoginPageView();
            }
        }

        Person person = personDAO.get(login.getPersonId());
        UserRole role = userRoleDAO.get((long)login.getRoleId());
        Company company = companyDAO.get(person.getCompanyId());

        logger.debug("Auth success for " + ulogin + " / " + role.getCode() + "/" + person.toDebugString());

        descriptor.getSession().setCompanyId(person.getCompanyId());
        descriptor.getSession().setLoginId(login.getId());
        descriptor.getSession().setPersonId(login.getPersonId());
        descriptor.getSession().setRoleId(login.getRoleId());
        descriptor.getSession().setExpired(DateUtils.addHours(new Date(), 3));
        descriptor.login(login,role,person,company);

        sessionDAO.removeByCondition("client_ip=? and login_id=?", descriptor.getSession().getClientIp(),
                login.getId());
        sessionDAO.persist(descriptor.getSession());

        return "redirect:/ws/";
        //return null;
    }
}