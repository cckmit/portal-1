package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class CardSqlBuilder {
    public SqlCondition createSqlCondition(CardQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                Condition searchCondition = SqlQueryBuilder.condition()
                        .or("serial_number").like(query.getSearchString())
                        .or("article").like(query.getSearchString())
                        .or("CO.info").like(query.getSearchString());
                condition.append(" and (")
                        .append(searchCondition.getSqlCondition())
                        .append(")");
                args.addAll(searchCondition.getSqlParameters());
            }

            if (isNotEmpty(query.getManagerIds())) {
                condition.append(" and CO.MANAGER IN ")
                        .append(makeInArg(query.getManagerIds()));
            }

            if (isNotEmpty(query.getTypeIds())) {
                condition.append(" and type_id in ")
                        .append(makeInArg(query.getTypeIds()));
            }

            if ( isNotEmpty(query.getStateIds()) ) {
                condition.append(" and CO.state in ")
                        .append(makeInArg(query.getStateIds()));
            }
        });
    }
}
