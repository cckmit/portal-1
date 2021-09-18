package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

@RemoteServiceRelativePath( "springGwtServices/CardController" )
public interface CardController extends RemoteService {

    SearchResult<Card> getCards(CardQuery query) throws RequestFailedException;

    Card getCard(Long id) throws RequestFailedException;
}
