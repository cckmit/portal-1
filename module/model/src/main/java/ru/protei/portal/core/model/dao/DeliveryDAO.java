package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.BaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;

/**
 * DAO для местоположений проекта
 */
public interface DeliveryDAO extends PortalBaseDAO<Delivery> {
    @SqlConditionBuilder
    SqlCondition baseQueryCondition (BaseQuery query);
}
