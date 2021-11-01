package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface PcbOrderDAO extends PortalBaseDAO<PcbOrder> {
    @SqlConditionBuilder
    SqlCondition createSqlCondition(PcbOrderQuery query);
}