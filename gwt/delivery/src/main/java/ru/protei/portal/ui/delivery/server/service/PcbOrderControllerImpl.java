package ru.protei.portal.ui.delivery.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.core.service.PcbOrderService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.PcbOrderController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service("PcbOrderController")
public class PcbOrderControllerImpl implements PcbOrderController {

    @Autowired
    PcbOrderService pcbOrderService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    @Override
    public SearchResult<PcbOrder> getPcbOrderList(PcbOrderQuery query) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(pcbOrderService.getPcbOrderList(token, query));
    }

    @Override
    public PcbOrder removePcbOrder(PcbOrder pcbOrder) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(pcbOrderService.removePcbOrder(token, pcbOrder));
    }
}
