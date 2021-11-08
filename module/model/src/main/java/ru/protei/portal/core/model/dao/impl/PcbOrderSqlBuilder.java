package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class PcbOrderSqlBuilder {
    public SqlCondition createSqlCondition(PcbOrderQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (isNotEmpty(query.getCardTypeIds())) {
                condition.append(" and pcb_order.card_type_id in ")
                        .append(makeInArg(query.getCardTypeIds()));
            }

            if (isNotEmpty(query.getTypeIds())) {
                condition.append(" and pcb_order.type in ")
                        .append(makeInArg(query.getTypeIds()));
            }

            if ( isNotEmpty(query.getStateIds()) ) {
                condition.append(" and pcb_order.state in ")
                        .append(makeInArg(query.getStateIds()));
            }

            if ( isNotEmpty(query.getPromptnessIds()) ) {
                condition.append(" and pcb_order.promptness in ")
                        .append(makeInArg(query.getPromptnessIds()));
            }
        });
    }
}
