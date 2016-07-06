package ru.protei.portal.webui.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.core.model.dao.UserSessionDAO;

/**
 * Created by michael on 27.06.16.
 */
@RestController
public class LogoutController {

    @Autowired
    UserSessionDAO sessionDAO;

}
