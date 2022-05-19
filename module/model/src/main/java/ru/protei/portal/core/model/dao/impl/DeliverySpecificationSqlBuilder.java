package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DeliverySpecificationQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class DeliverySpecificationSqlBuilder {
    public SqlCondition createSqlCondition(DeliverySpecificationQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                condition.append(" and name like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }
        });
    }
}
