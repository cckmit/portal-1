package ru.protei.portal.webui.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.core.model.dao.UserSessionDAO;
import ru.protei.portal.core.service.user.UserSessionDescriptor;
import ru.protei.portal.webui.api.CoreResponse;

/**
 * Created by michael on 27.06.16.
 */
@RestController
public class LogoutController {

    @Autowired
    UserSessionDAO sessionDAO;

}
