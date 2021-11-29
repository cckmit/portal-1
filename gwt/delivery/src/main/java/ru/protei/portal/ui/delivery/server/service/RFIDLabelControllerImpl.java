package ru.protei.portal.ui.delivery.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.core.service.RFIDLabelService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.RFIDLabelController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service("RFIDLabelController")
public class RFIDLabelControllerImpl implements RFIDLabelController {

    private static final Logger log = LoggerFactory.getLogger(RFIDLabelControllerImpl.class);

    @Autowired
    RFIDLabelService rfidLabelService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;


    @Override
    public RFIDLabel get(Long id) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(rfidLabelService.get(token, id));
    }

    @Override
    public RFIDLabel update(RFIDLabel value) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(rfidLabelService.update(token, value));
    }

    @Override
    public RFIDLabel remove(RFIDLabel value) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(rfidLabelService.remove(token, value));
    }

    @Override
    public RFIDLabel getLastScanLabel(boolean start) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(rfidLabelService.getLastScanLabel(token, start));
    }
}
