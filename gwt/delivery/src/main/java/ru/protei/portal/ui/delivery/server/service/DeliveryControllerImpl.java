package ru.protei.portal.ui.delivery.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.struct.delivery.DeliveryNameAndDescriptionChangeRequest;
import ru.protei.portal.core.service.DeliveryService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.DeliveryController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;

import static ru.protei.portal.ui.common.server.ServiceUtils.*;

@Service("DeliveryController")
public class DeliveryControllerImpl implements DeliveryController {

    @Override
    public SearchResult<Delivery> getDeliveries(BaseQuery query) throws RequestFailedException {
        log.info("getDeliveries(): query={}", query);
        Result<SearchResult<Delivery>> result = deliveryService.getDeliveries(getAuthToken(sessionService, httpRequest), query);
        log.info("getDeliveries(): result={}", result);
        return checkResultAndGetData(result);
    }

    @Override
    public Delivery getDelivery(long id) throws RequestFailedException {
        Result<Delivery> result = deliveryService.getDelivery(getAuthToken(sessionService, httpRequest), id);
        return checkResultAndGetData(result);
    }

    @Override
    public Delivery saveDelivery(Delivery delivery) throws RequestFailedException {
        if (delivery == null) {
            log.warn("null delivery in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.info("saveDelivery, id: {}", HelperFunc.nvlt(delivery.getId(), "new"));

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<Delivery> response = ( delivery.getId() == null ) ?
                deliveryService.createDelivery(token, delivery)
                : deliveryService.updateDelivery(token, delivery);

        log.info("saveDelivery, result: {}", response.isOk() ? "ok" : response.getStatus());

        return checkResultAndGetData(response);
    }

    @Override
    public String getLastSerialNumber(boolean isArmyProject) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(deliveryService.getLastSerialNumber(token, isArmyProject));
    }

    @Override
    public void saveNameAndDescription(DeliveryNameAndDescriptionChangeRequest changeRequest)  throws RequestFailedException {
        log.info("saveIssueNameAndDescription(): id={}| name={}, description={}", changeRequest.getId(), changeRequest.getName(), changeRequest.getDescription());
        AuthToken token = getAuthToken(sessionService, httpRequest);
        Result response = deliveryService.updateNameAndDescription(token, changeRequest);
        log.info("saveIssueNameAndDescription(): response.isOk()={}", response.isOk());

        checkResult(response);
    }

    @Autowired
    DeliveryService deliveryService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(DeliveryControllerImpl.class);
}
