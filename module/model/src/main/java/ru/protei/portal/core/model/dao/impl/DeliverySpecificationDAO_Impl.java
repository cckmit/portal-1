package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DeliverySpecificationDAO;
import ru.protei.portal.core.model.ent.DeliverySpecification;
import ru.protei.portal.core.model.query.DeliverySpecificationQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class DeliverySpecificationDAO_Impl extends PortalBaseJdbcDAO<DeliverySpecification> implements DeliverySpecificationDAO {
    @Autowired
    DeliverySpecificationSqlBuilder sqlBuilder;

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(DeliverySpecificationQuery query) {
        return sqlBuilder.createSqlCondition(query);
    }
}
