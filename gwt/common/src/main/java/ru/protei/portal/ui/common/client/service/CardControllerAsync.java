package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface CardControllerAsync {

    void getCards(CardQuery query, AsyncCallback<SearchResult<Card>> async) throws RequestFailedException;

    void getCard(Long id, AsyncCallback<Card> async) throws RequestFailedException;
}
