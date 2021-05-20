package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.SqlCondition;

/**
 * DAO для поставок
 */
public interface DeliveryDAO extends PortalBaseDAO<Delivery> {
    SqlCondition baseQueryCondition (DeliveryQuery query);
}
