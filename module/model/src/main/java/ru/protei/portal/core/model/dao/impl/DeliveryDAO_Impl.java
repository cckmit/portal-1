package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DeliveryDAO;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.SqlCondition;

/**
 * DAO для поставок
 */
public class DeliveryDAO_Impl extends PortalBaseJdbcDAO<Delivery> implements DeliveryDAO {

    @Autowired
    DeliverySqlBuilder deliverySqlBuilder;

    @Override
    @SqlConditionBuilder
    public SqlCondition baseQueryCondition(DeliveryQuery query) {
        return deliverySqlBuilder.getCondition(query);
    }
}
