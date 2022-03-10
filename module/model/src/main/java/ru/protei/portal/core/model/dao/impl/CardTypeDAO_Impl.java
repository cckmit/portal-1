package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CardTypeDAO;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.query.CardTypeQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class CardTypeDAO_Impl extends PortalBaseJdbcDAO<CardType> implements CardTypeDAO {

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CardTypeQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getDisplay() != null) {
                condition.append(" and is_display = ?");
                args.add(query.getDisplay());
            }
        });
    }
}
