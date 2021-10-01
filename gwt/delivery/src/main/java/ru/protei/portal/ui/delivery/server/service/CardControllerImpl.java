package ru.protei.portal.ui.delivery.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.CardTypeQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.CardService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.CardController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service("CardController")
public class CardControllerImpl implements CardController {

    @Autowired
    CardService cardService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    @Override
    public SearchResult<Card> getCards(CardQuery query) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardService.getCards(token, query));
    }

    @Override
    public Card getCard(Long id) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardService.getCard(token, id));
    }

    @Override
    public List<EntityOption> getCardTypeOptionList(CardTypeQuery query) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardService.getCardTypeOptionList(token, query));
    }

    @Override
    public List<CardType> getCardTypeList() throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardService.getCardTypeList(token));
    }

    @Override
    public Card createCard(Card card) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardService.createCard(token, card));
    }

    @Override
    public Card updateMeta(Card card) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardService.updateMeta(token, card));
    }

    @Override
    public Long getSizeByBatchId(Long cardBatchId) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpRequest);
        return checkResultAndGetData(cardService.getSizeByBatchId(token, cardBatchId));
    }
}
