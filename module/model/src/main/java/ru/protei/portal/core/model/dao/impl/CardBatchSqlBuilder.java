package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;

public class CardBatchSqlBuilder {
    public SqlCondition createSqlCondition(CardBatchQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                Condition searchCondition = SqlQueryBuilder.condition();
                condition.append(" and (")
                         .append(searchCondition.getSqlCondition())
                         .append(")");

                args.addAll(searchCondition.getSqlParameters());
            }

            // todo add conditions
        });
    }
}
