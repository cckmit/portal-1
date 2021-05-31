package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.beans.SearchResult;

/**
 * DAO для поставок
 */
public interface DeliveryDAO extends PortalBaseDAO<Delivery> {
    SqlCondition baseQueryCondition (DeliveryQuery query);

    SearchResult<Delivery> getSearchResult(DeliveryQuery query);
}
