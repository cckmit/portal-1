package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CardGroupChangeRequest;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.CardTypeQuery;
import ru.protei.portal.core.model.util.UiResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Set;

@RemoteServiceRelativePath( "springGwtServices/CardController" )
public interface CardController extends RemoteService {

    SearchResult<Card> getCards(CardQuery query) throws RequestFailedException;

    Card getCard(Long id) throws RequestFailedException;

    List<EntityOption> getCardTypeOptionList(CardTypeQuery query) throws RequestFailedException;

    List<CardType> getCardTypeList() throws RequestFailedException;

    Card createCard(Card card) throws RequestFailedException;

    Card updateNoteAndComment(Card card) throws RequestFailedException;

    Card updateMeta(Card card) throws RequestFailedException;

    UiResult<Set<Card>> updateCards(CardGroupChangeRequest cards) throws RequestFailedException;

    Card removeCard(Card card) throws RequestFailedException;

    Long getLastNumber(Long typeId, Long cardBatchId) throws RequestFailedException;
}
