package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.query.CardTypeQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface CardTypeDAO extends PortalBaseDAO<CardType> {

    @SqlConditionBuilder
    SqlCondition createSqlCondition(CardTypeQuery query);
}