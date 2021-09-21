package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CardDAO;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class CardDAO_Impl extends PortalBaseJdbcDAO<Card> implements CardDAO {

    @Autowired
    CardSqlBuilder cardSqlBuilder;

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CardQuery query) {
        return cardSqlBuilder.createSqlCondition(query);
    }
}
