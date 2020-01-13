package ru.protei.portal.app.portal.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.PingController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;

/**
 * Пингер
 */
@Service( "PingController" )
public class PingControllerImpl implements PingController {

    @Override
    public void ping() throws RequestFailedException {
        ServiceUtils.getAuthToken(sessionService, httpRequest);
        sessionService.setSessionLifetime(httpRequest, AuthService.DEF_APP_SESSION_LIVE_TIME);
    }

    @Autowired
    HttpServletRequest httpRequest;
    @Autowired
    SessionService sessionService;
}
