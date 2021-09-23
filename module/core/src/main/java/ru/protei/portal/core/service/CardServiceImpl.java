package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CardDAO;
import ru.protei.portal.core.model.dao.CardTypeDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.CardTypeQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class CardServiceImpl implements CardService {

    @Autowired
    CardDAO cardDAO;

    @Autowired
    CardTypeDAO cardTypeDAO;

    @Override
    public Result<Card> getCard(AuthToken token, Long id) {
        Card card = cardDAO.get(id);

        if (card == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(card);
    }

    @Override
    public Result<SearchResult<Card>> getCards(AuthToken token, CardQuery query) {
        SearchResult<Card> sr = cardDAO.getSearchResultByQuery(query);
        return ok(sr);
    }

    @Override
    public Result<List<EntityOption>> getCardTypeOptionList(AuthToken token, CardTypeQuery query) {
        List<CardType> result = cardTypeDAO.listByQuery(query);

        if (result == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        List<EntityOption> options = result.stream()
                .filter(cardType -> query.getDisplay() == null || Objects.equals(cardType.isDisplay(), query.getDisplay()))
                .map(ct -> new EntityOption(ct.getName(), ct.getId()))
                .collect(Collectors.toList());

        return ok(options);
    }
}
