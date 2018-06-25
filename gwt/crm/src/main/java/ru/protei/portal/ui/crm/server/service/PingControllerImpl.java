package ru.protei.portal.ui.crm.server.service;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.dao.UserSessionDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.ui.common.client.service.PingController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Пингер
 */
@Service( "PingController" )
public class PingControllerImpl implements PingController {

    @Override
    public void ping() throws RequestFailedException {
        UserSessionDescriptor usd = sessionService.getUserSessionDescriptor( httpRequest );
        if ( usd == null ) {
            throw new RequestFailedException( En_ResultStatus.INVALID_SESSION_ID );
        }

        usd.getSession().setExpired(DateUtils.addHours(new Date(), 3));
    }

    @Autowired
    HttpServletRequest httpRequest;

    @Autowired
    SessionService sessionService;

    @Autowired
    UserSessionDAO sessionDAO;
}
