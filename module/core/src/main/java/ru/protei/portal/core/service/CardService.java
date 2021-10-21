package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.CardTypeQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface CardService {
    @Privileged({ En_Privilege.CARD_VIEW })
    Result<Card> getCard(AuthToken token, Long id);

    @Privileged({ En_Privilege.CARD_VIEW })
    Result<SearchResult<Card>> getCards(AuthToken token, CardQuery query);

    @Privileged({ En_Privilege.CARD_CREATE })
    @Auditable( En_AuditType.CARD_CREATE )
    Result<Card> createCard(AuthToken token, Card card);

    @Privileged({ En_Privilege.CARD_EDIT })
    @Auditable( En_AuditType.CARD_MODIFY)
    Result<Card> updateNoteAndComment(AuthToken token, Card card);

    @Privileged({ En_Privilege.CARD_EDIT })
    @Auditable( En_AuditType.CARD_MODIFY)
    Result<Card> updateMeta(AuthToken token, Card card);

    @Privileged({ En_Privilege.CARD_REMOVE })
    @Auditable( En_AuditType.CARD_REMOVE )
    Result<Card> removeCard(AuthToken token, Card card);

    Result<List<EntityOption>> getCardTypeOptionList(AuthToken token, CardTypeQuery query);

    Result<List<CardType>> getCardTypeList(AuthToken token);

    Result<Long> getLastNumber(AuthToken token, Long typeId, Long cardBatchId);
}
