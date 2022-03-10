package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CardCreateRequest;
import ru.protei.portal.core.model.ent.CardGroupChangeRequest;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.CardTypeQuery;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Set;

public interface CardControllerAsync {

    void getCards(CardQuery query, AsyncCallback<SearchResult<Card>> async);

    void getCard(Long id, AsyncCallback<Card> async);

    void getCardTypeOptionList(CardTypeQuery query, AsyncCallback<List<EntityOption>> async);

    void getCardTypeList(CardTypeQuery query, AsyncCallback<List<CardType>> async);

    void createCards(CardCreateRequest createRequest, AsyncCallback<List<Card>> async);

    void updateNoteAndComment(Card card, AsyncCallback<Card> async);

    void updateMeta(Card card, AsyncCallback<Card> async);

    void updateCards(CardGroupChangeRequest cards, AsyncCallback<UiResult<Set<Card>>> async);

    void removeCard(Card card, AsyncCallback<Card> async);

    void getLastNumber(Long typeId, Long cardBatchId, AsyncCallback<Long> async);
}
