package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface CardService {
    @Privileged({ En_Privilege.DELIVERY_VIEW })
    Result<Card> getCard(AuthToken token, Long id);

    @Privileged({ En_Privilege.DELIVERY_VIEW })
    Result<SearchResult<Card>> getCards(AuthToken token, CardQuery query);
}
