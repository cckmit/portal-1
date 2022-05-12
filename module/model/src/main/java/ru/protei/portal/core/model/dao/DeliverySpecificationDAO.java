package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.DeliverySpecification;
import ru.protei.portal.core.model.query.DeliverySpecificationQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface DeliverySpecificationDAO extends PortalBaseDAO<DeliverySpecification> {
    @SqlConditionBuilder
    SqlCondition createSqlCondition(DeliverySpecificationQuery query);
}
