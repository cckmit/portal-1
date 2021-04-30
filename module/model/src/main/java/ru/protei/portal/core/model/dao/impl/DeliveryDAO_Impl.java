package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DeliveryDAO;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;

/**
 * DAO для местоположений проекта
 */
public class DeliveryDAO_Impl extends PortalBaseJdbcDAO<Delivery> implements DeliveryDAO {
    @Override
    @SqlConditionBuilder
    public SqlCondition baseQueryCondition(BaseQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1 = 1");
        });
    }
}
