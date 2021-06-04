package ru.protei.portal.ui.delivery.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObjectMetaNotifiers;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.service.CaseService;
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
    public SearchResult<Delivery> getDeliveries(DeliveryQuery query) throws RequestFailedException {
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
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(deliveryService.createDelivery(token, delivery));
    }

    @Override
    public long removeDelivery(Long id) throws RequestFailedException {
        log.info("removeDelivery(): id={}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<Long> response = deliveryService.removeDelivery(token, id);
        log.info("removeDelivery(): id={}, result={}", id, response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public String getLastSerialNumber(boolean isArmyProject) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(deliveryService.getLastSerialNumber(token, isArmyProject));
    }

    @Override
    public void updateNameAndDescription(CaseNameAndDescriptionChangeRequest changeRequest)  throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        checkResult(caseService.updateCaseNameAndDescription(token, changeRequest, En_CaseType.DELIVERY));
    }

    @Override
    public Delivery updateMeta(Delivery meta) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(deliveryService.updateMeta(token, meta));
    }

    @Override
    public CaseObjectMetaNotifiers updateMetaNotifiers(CaseObjectMetaNotifiers caseMetaNotifiers) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(caseService.updateCaseObjectMetaNotifiers(token, En_CaseType.DELIVERY, caseMetaNotifiers));
    }


    @Autowired
    DeliveryService deliveryService;
    @Autowired
    CaseService caseService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(DeliveryControllerImpl.class);
}
