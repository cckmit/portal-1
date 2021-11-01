package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class PcbOrderSqlBuilder {
    public SqlCondition createSqlCondition(PcbOrderQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                Condition searchCondition = SqlQueryBuilder.condition()
                        .or("modification").like(query.getSearchString())
                        .or("comment").like(query.getSearchString());
                condition.append(" and (")
                        .append(searchCondition.getSqlCondition())
                        .append(")");
                args.addAll(searchCondition.getSqlParameters());
            }

            if (isNotEmpty(query.getCardTypeIds())) {
                condition.append(" and card.type_id in ")
                        .append(makeInArg(query.getCardTypeIds()));
            }

            if (isNotEmpty(query.getTypeIds())) {
                condition.append(" and type IN ")
                        .append(makeInArg(query.getTypeIds()));
            }

            if ( isNotEmpty(query.getStateIds()) ) {
                condition.append(" and state in ")
                        .append(makeInArg(query.getStateIds()));
            }

            if ( isNotEmpty(query.getPromptnessIds()) ) {
                condition.append(" and promptness in ")
                        .append(makeInArg(query.getPromptnessIds()));
            }
        });
    }
}
