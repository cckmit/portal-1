package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.CardTypeQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface CardControllerAsync {

    void getCards(CardQuery query, AsyncCallback<SearchResult<Card>> async);

    void getCard(Long id, AsyncCallback<Card> async);

    void getCardTypeOptionList(CardTypeQuery query, AsyncCallback<List<EntityOption>> async);

    void createCard(Card card, AsyncCallback<Card> async);

    void updateMeta(Card card, AsyncCallback<Card> async);
}
