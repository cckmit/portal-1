package ru.protei.portal.ui.crm.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.ui.common.client.service.PingService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;

/**
 * Пингер
 */
@Service( "PingService" )
public class PingServiceImpl implements PingService {

    @Override
    public void ping() throws RequestFailedException {
        if ( sessionService.getUserSessionDescriptor( httpRequest ) == null ) {
            throw new RequestFailedException( En_ResultStatus.INVALID_SESSION_ID );
        }
    }

    @Autowired
    HttpServletRequest httpRequest;

    @Autowired
    SessionService sessionService;
}
