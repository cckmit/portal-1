package ru.protei.portal.ui.delivery.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.service.CardBatchService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.CardBatchController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service("CardBatchController")
public class CardBatchControllerImpl implements CardBatchController {

    @Autowired
    CardBatchService cardBatchService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    @Override
    public CardBatch saveCardBatch(CardBatch cardBatch) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardBatchService.createCardBatch(token, cardBatch));
    }

    @Override
    public CardBatch getLastCardBatch(Long typeId) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardBatchService.getLastCardBatch(token, typeId));
    }

    @Override
    public CardBatch updateMeta(CardBatch meta) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardBatchService.updateMeta(token, meta));
    }

    @Override
    public CardBatch getCardBatch(Long id) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardBatchService.getCardBatch(token, id));
    }

    @Override
    public CardBatch updateCardBatch(CardBatch cardBatch) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardBatchService.updateCardBatch(token, cardBatch));
    }

    @Override
    public List<CardBatch> getListCardBatchByType(CardType cardType) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardBatchService.getListCardBatchByType(token, cardType));
    }
}
