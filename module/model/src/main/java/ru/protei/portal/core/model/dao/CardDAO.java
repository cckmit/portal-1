package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface CardDAO extends PortalBaseDAO<Card> {
    @SqlConditionBuilder
    SqlCondition createSqlCondition(CardQuery query);
}