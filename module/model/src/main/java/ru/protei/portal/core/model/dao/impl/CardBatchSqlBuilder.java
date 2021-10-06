package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;

import java.util.HashSet;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class CardBatchSqlBuilder {
    public SqlCondition createSqlCondition(CardBatchQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                Condition searchCondition = SqlQueryBuilder.condition()
                        .or("number").like(query.getSearchString())
                        .or("article").like(query.getSearchString());
                condition.append(" and (")
                        .append(searchCondition.getSqlCondition())
                        .append(")");

                args.addAll(searchCondition.getSqlParameters());
            }

            if (isNotEmpty(query.getTypeIds())) {
                condition.append(" and type_id in ")
                         .append(makeInArg(query.getTypeIds()));
            }

            if (isNotEmpty(query.getStateIds())) {
                condition.append(" and CO.state in ")
                         .append(makeInArg(query.getStateIds()));
            }

            if (isNotEmpty(query.getImportanceIds())) {
                condition.append(" and CO.importance in ")
                         .append(makeInArg(query.getImportanceIds()));
            }

            Interval deadlineInterval = makeInterval(query.getDeadline());
            if (deadlineInterval != null) {
                if (deadlineInterval.from != null) {
                    condition.append(" and CO.deadline >= ?");
                    args.add(deadlineInterval.from.getTime());
                }

                if (deadlineInterval.to != null) {
                    condition.append(" and CO.deadline < ?");
                    args.add(deadlineInterval.to.getTime());
                }
            }

            if (isNotEmpty(query.getContractors())) {
                Set<Long> caseMemberIds = new HashSet<>(query.getContractors());
                condition.append(" and CO.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ID IN ")
                         .append(makeInArg(caseMemberIds, false))
                         .append(")");
            }
        });
    }
}
